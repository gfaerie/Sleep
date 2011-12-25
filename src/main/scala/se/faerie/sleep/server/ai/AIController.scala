package se.faerie.sleep.server.ai

import se.faerie.sleep.server.ai.AIState._
import se.faerie.sleep.server.state.update.GameStateUpdateContext
import se.faerie.sleep.server.state.update.GameStateUpdater
import se.faerie.sleep.server.state.GameObjectMetadata
import se.faerie.sleep.common.MapPosition
import se.faerie.sleep.server.state.GameObject
import scala.collection.mutable.HashMap
import scala.collection.mutable.MultiMap
import se.faerie.sleep.common.GraphicsHelper
import se.faerie.sleep.common.pathfinding.PathFinder

/**
 *
 * Goals:
 *
 * 1) Get prototype working with melee
 * 2) Worry about performance
 * 3) Model better
 * 4) Add ranged combat and other skills
 *
 */
class AIController(val updateInterval: Long, actionFactory: AIActionFactory) extends GameStateUpdater with GraphicsHelper {

  class TargetEvaluation(val target: GameObject, val range: Double, val blockedTiles: Int, val freeTiles: Int, val score: Double)

  priority = 500;

  def update(context: GameStateUpdateContext): Unit = {
    val players = getPlayers(context)
    groupsToUpdate(context).foreach(g => {
      handleGroup(context, players, g._1, g._2);
    });

  }

  def handleGroup(context: GameStateUpdateContext,
    players: HashMap[MapPosition, GameObject],
    group: AIGroup,
    groupContents: Traversable[AIControlledGameObject]) = {

    val state = context.state;

    // remove group target if it no longer exists
    if (group.groupTarget != null && (!context.state.objectExists(group.groupTarget))) {
      group.groupTarget = null;
    }

    // for each monster in group
    groupContents.filter(f => (f.lastUpdated + updateInterval) < context.updateTime).foreach(m => {

      // remove last attacker if it no longer exists
      if (m.lastAttacker != null && (!context.state.objectExists(m.lastAttacker))) {
        m.lastAttacker = null;
      }

      m.state match {
        case Sleeping => {
          val targetEval = evaluateTargets(context, players, m, group, null);
          if (targetEval != null) {
            attackTarget(context, m, group, targetEval);
          }
        }
        case p: Patrolling => {
          val targetEval = evaluateTargets(context, players, m, group, null);
          if (targetEval != null) {
            attackTarget(context, m, group, targetEval);
          } else if (m.movement == null) {
            // plot path to new position
          }
        }
        case p: Pursuing => {
          val targetEval = evaluateTargets(context, players, m, group, p.target);
          // aggro lost
          if (targetEval == null) {

          } // declare new attack if new target or target is in view or we have stopped
          else if ((targetEval.target.id != p.target) || (targetEval.blockedTiles == 0) || (m.movement == null)) {
            attackTarget(context, m, group, targetEval);
          }

        }
        case a: Attacking => {
          val targetEval = evaluateTargets(context, players, m, group, a.target);
          // aggro lost
          if (targetEval == null) {

          } // declare new attack if new target or target is not in view or we have stopped
          else if ((targetEval.target.id != a.target) || (targetEval.blockedTiles > 0) || (m.movement == null)) {
            attackTarget(context, m, group, targetEval);
          }
        }
      }

      m.lastUpdated = context.updateTime
    })

    // assign new grouptarget
  }

  def attackTarget(context: GameStateUpdateContext, attacker: AIControlledGameObject, group: AIGroup, targetEval: TargetEvaluation) {
    // can we see the target?
    if (targetEval.blockedTiles == 0) {
      attacker.state = Attacking(targetEval.target.id)
      context.addUpdater(actionFactory.createMeleeAction(attacker.id, targetEval.target.id, -1));
    } // else pursue
    else {
      attacker.state = Pursuing(targetEval.target.id)
      context.addUpdater(actionFactory.createPursuitAction(attacker.id, context.state.getObjectPosition(targetEval.target.id), -1));
    }
    if (group.groupTarget == null) {
      group.groupTarget = targetEval.target.id;
    }
  }

  def evaluateTargets(context: GameStateUpdateContext, players: HashMap[MapPosition, GameObject], monster: AIControlledGameObject, group: AIGroup, currentTarget: java.lang.Long): TargetEvaluation = {
    var target: TargetEvaluation = null;
    val currentPosition = context.state.getObjectPosition(monster.id);
    players.foreach(p => {
      val range = currentPosition.distanceTo(p._1);
      if (range < monster.aggressionHandler.maxRange) {
        var score = 0.0;

        // get distance and calc blocked and free tiles. NOTE: this is most likely the performance bottleneck but i really like to avoid radius only triggers
        var freeTiles = 0;
        var solidTiles = 0;
        buildLine(currentPosition.x, currentPosition.y, p._1.x, p._1.y).foreach(t => {
          if (context.state.getBackground(t.x, t.y).solid) {
            solidTiles += 1;
          } else {
            freeTiles += 1;
          }
        });
        score += monster.aggressionHandler.blockedTileBonus(solidTiles);
        score += monster.aggressionHandler.freeTileBonus(solidTiles);

        // add bonus for this player type
        score += monster.aggressionHandler.objectBonus(p._2);

        // add bonuses for targets and attackers
        if (currentTarget != null && (currentTarget == p._2.id)) {
          score += monster.aggressionHandler.currentTargetBonus;
        }
        if (group.groupTarget != null && (group.groupTarget == p._2.id)) {
          score += monster.aggressionHandler.groupTargetBonus;
        }
        if (monster.lastAttacker != null && (monster.lastAttacker == p._2.id)) {
          score += monster.aggressionHandler.latestAttackerBonus;
        }

        // is this the new top target? 
        if (score > monster.aggressionHandler.aggroLimit && (target == null || score > target.score)) {
          target = new TargetEvaluation(p._2, range, solidTiles, freeTiles, score);
        }
      }

    })
    return target;
  }

  def getPlayers(context: GameStateUpdateContext): HashMap[MapPosition, GameObject] = {
    val players = HashMap[MapPosition, GameObject]()
    context.state.getObjects(GameObjectMetadata.Player).foreach(o => {
      players += (context.state.getObjectPosition(o.id) -> o)
    });
    return players;

  }

  def groupsToUpdate(context: GameStateUpdateContext): MultiMap[AIGroup, AIControlledGameObject] = {
    val returnMap = new HashMap[AIGroup, collection.mutable.Set[AIControlledGameObject]] with MultiMap[AIGroup, AIControlledGameObject];
    context.state.getObjects(GameObjectMetadata.AIControlled).foreach(o => {
      o match {
        case g: AIControlledGameObject => {
          returnMap.addBinding(g.group, g);
        }
      }
    });
    return returnMap;
  }
}