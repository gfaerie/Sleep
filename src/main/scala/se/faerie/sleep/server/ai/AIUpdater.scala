package se.faerie.sleep.server.ai

import se.faerie.sleep.server.ai.AIState._
import se.faerie.sleep.server.state.update.GameStateUpdateContext
import se.faerie.sleep.server.state.update.GameStateUpdater
import se.faerie.sleep.server.state.GameObjectMetadata
import se.faerie.sleep.common.MapPosition
import se.faerie.sleep.server.state.GameObject
import scala.collection.mutable.HashMap
import scala.collection.mutable.Map
import scala.collection.mutable.MultiMap
import se.faerie.sleep.common.GraphicsHelper
import se.faerie.sleep.common.pathfinding.PathFinder

/**
 *
 * Simply updates the ai controlled objects
 *
 * Goals:
 *
 * 1) Get prototype working with melee
 * 2) Worry about performance
 * 3) Model better
 * 4) Add ranged combat and other skills
 *
 */
class AIUpdater(val updateInterval: Long, actionFactory: AIActionFactory) extends GameStateUpdater with GraphicsHelper {

  class TargetEvaluation(val target: GameObject, val range: Double, val blockedTiles: Int, val freeTiles: Int, val score: Double)

  priority = 500;

  def update(context: GameStateUpdateContext): Unit = {
    val players = getPlayers(context)
    context.state.getObjects(GameObjectMetadata.AIControlled).foreach(o => {
      o match {
        case a: AIControlledGameObject => {
          if ((a.lastUpdated + updateInterval) < context.updateTime) {
            handleObject(context, players, a);
          }
        }
      }
    });

  }

  def handleObject(context: GameStateUpdateContext,
                   players: Map[MapPosition, GameObject],
                   m: AIControlledGameObject) = {

    val state = context.state;
    val group = m.group;

    // remove group target if it no longer exists
    if (group.groupTarget != null && (!context.state.objectExists(group.groupTarget))) {
      group.groupTarget = null;
    }

    // remove last attacker if it no longer exists
    if (m.lastAttacker != null && (!context.state.objectExists(m.lastAttacker))) {
      m.lastAttacker = null;
    }

    m.state match {
      case p: Patrolling => {
        val targetEval = evaluateTargets(context, players, m, group, null);
        if (targetEval != null) {
          attackTarget(context, m, targetEval);
        }
        else if (m.movement == null) {
          noTarget(context, m);
        }
      }
      case p: Pursuing => {
        val targetEval = evaluateTargets(context, players, m, group, p.target);
        // aggro lost
           // remove group target if it no longer exists
        if (!context.state.objectExists(p.target) || (targetEval == null)) {
          noTarget(context, m);
        }
        // declare new attack if new target or target is in view or we have stopped
        else if ((targetEval.target.id != p.target) || (targetEval.blockedTiles == 0) || (m.movement == null)) {
          attackTarget(context, m, targetEval);
        }
        else if (p.pursuitStarted + m.aggressionHandler.maxPursuitTime > context.updateTime) {
          noTarget(context, m);
        }
      }

      case a: Attacking => {
        val targetEval = evaluateTargets(context, players, m, group, a.target);
        // aggro lost
        if (targetEval == null) {
          noTarget(context, m);
        }
        // declare new attack if new target or target is not in view or we have stopped
        else if ((targetEval.target.id != a.target) || (targetEval.blockedTiles > 0) || (m.movement == null)) {
          attackTarget(context, m, targetEval);
        }
      }
    }

    m.lastUpdated = context.updateTime
  }

  def noTarget(context: GameStateUpdateContext, idle: AIControlledGameObject) {
    // TODO select next patrol point and set next update point will require access to context
    // group has target and we are far from it, go there
    if (context.state.getObjectPosition(idle.id).distanceTo(idle.group.rallyPoint) > idle.aggressionHandler.patrolLimit) {
      context.addUpdater(actionFactory.createPatrolAction(idle, idle.group))
    }
  }

  def attackTarget(context: GameStateUpdateContext, attacker: AIControlledGameObject, targetEval: TargetEvaluation) {
    // can we see the target?
    if (targetEval.blockedTiles == 0) {
      attacker.state = Attacking(targetEval.target.id)
      context.addUpdater(actionFactory.createMeleeAction(attacker, targetEval.target.id, -1));
    } // else pursue
    else {
      attacker.state = Pursuing(targetEval.target.id, System.nanoTime(), true)
      context.addUpdater(actionFactory.createPursuitAction(attacker, context.state.getObjectPosition(targetEval.target.id), -1));
    }

    // alert the rest of the group that we got a new target
    if (attacker.group.groupTarget == null) {
      attacker.group.groupTarget = targetEval.target.id;
    }
  }

  def evaluateTargets(context: GameStateUpdateContext, players: Map[MapPosition, GameObject], monster: AIControlledGameObject, group: AIGroup, currentTarget: java.lang.Long): TargetEvaluation = {
    var target: TargetEvaluation = null;
    val currentPosition = context.state.getObjectPosition(monster.id);
    players.foreach(p => {
      val range = currentPosition.distanceTo(p._1);
      val isCurrentTarget = currentTarget != null && (currentTarget == p._2.id);
      val isGroupTarget = group.groupTarget != null && (group.groupTarget == p._2.id)
      val isLastAttacker = monster.lastAttacker != null && (monster.lastAttacker == p._2.id)

      // should we check at all?
      if (range < monster.aggressionHandler.maxRange ||
        isCurrentTarget ||
        isGroupTarget ||
        isLastAttacker) {
        
        // get distance and calc blocked and free tiles. NOTE: this is most likely the performance bottleneck but i really like to avoid radius only triggers
        val (freeTiles, solidTiles) = {
          val solidsAndFress = buildLine(currentPosition.x, currentPosition.y, p._1.x, p._1.y).partition(t => context.state.getBackground(t.x, t.y).solid);
          (solidsAndFress._2.size, solidsAndFress._1.size)
        }

        val score = monster.aggressionHandler.tileBonus(freeTiles, solidTiles)
        +(if (isCurrentTarget) monster.aggressionHandler.currentTargetBonus else 0)
        +(if (isGroupTarget) monster.aggressionHandler.groupTargetBonus else 0)
        +(if (isLastAttacker) monster.aggressionHandler.latestAttackerBonus else 0)
        +monster.aggressionHandler.objectBonus(p._2)
        
        // is this the new top target? 
        if (score > monster.aggressionHandler.aggroLimit && (target == null || score > target.score)) {
          target = new TargetEvaluation(p._2, range, solidTiles, freeTiles, score);
        }
      }
    })
    return target;
  }

  def getPlayers(context: GameStateUpdateContext): Map[MapPosition, GameObject] = {
    val players = HashMap[MapPosition, GameObject]()
    context.state.getObjects(GameObjectMetadata.Player).foreach(o => {
      players += (context.state.getObjectPosition(o.id) -> o)
    });
    return players;
  }
}