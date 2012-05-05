package se.faerie.sleep.server.ai

import scala.collection.mutable.HashMap
import scala.collection.mutable.Map

import se.faerie.sleep.common.GraphicsHelper
import se.faerie.sleep.common.MapPosition
import se.faerie.sleep.server.ai.AIState.Attacking
import se.faerie.sleep.server.ai.AIState.Patrolling
import se.faerie.sleep.server.ai.AIState.Pursuing
import se.faerie.sleep.server.player.PlayerMetadata
import se.faerie.sleep.server.state.update.GameStateUpdateContext
import se.faerie.sleep.server.state.update.GameStateUpdater
import se.faerie.sleep.server.state.GameObject

/**
 *
 * Simply updates the ai controlled objects. Simple kind of state machines.
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

    // can I get a U, can I get a G , can I get a L, can I get a Y! What does it spell?
    context.state.getObjects(AIControlMetadata.getClass()).foreach(o => {
      o.staticMetadata.find(a => a.getClass().equals(AIControlMetadata.getClass())) match {
        case Some(data) => data match {
          case metadata: AIControlMetadata => {
            if (metadata.lastUpdated + updateInterval < context.updateTime) {
              handleObject(context, players, o, metadata);
            }
          }
        }
        case None => {
          // should never happen...log?
        }
      }
    });
  }

  def handleObject(context: GameStateUpdateContext,
                   players: Map[MapPosition, GameObject],
                   aiObject: GameObject, metadata: AIControlMetadata) = {

    val state = context.state;
    val group = metadata.group;

    // remove group target if it no longer exists
    if (group.groupTarget != null && (!context.state.objectExists(group.groupTarget))) {
      group.groupTarget = null;
    }

    // remove last attacker if it no longer exists
    if (metadata.lastAttacker != null && (!context.state.objectExists(metadata.lastAttacker))) {
      metadata.lastAttacker = null;
    }

    if ((group.lastUpdated + updateInterval) < context.updateTime) {
      updateGroupRallyPoint(context, players, group);
    }

    metadata.state match {
      case p: Patrolling => {
        val targetEval = evaluateTargets(context, players, aiObject, metadata, group, null);
        if (targetEval != null) {
          attackTarget(context, aiObject, metadata, targetEval);
        }
        else if (aiObject.movement == null) {
          noTarget(context, aiObject, metadata);
        }
      }
      case p: Pursuing => {
        val targetEval = evaluateTargets(context, players, aiObject, metadata, group, p.target);
        // aggro lost
        // remove group target if it no longer exists
        if (!context.state.objectExists(p.target) || (targetEval == null)) {
          noTarget(context, aiObject, metadata);
        }
        // declare new attack if new target or target is in view or we have stopped
        else if ((targetEval.target.id != p.target) || (targetEval.blockedTiles == 0) || (aiObject.movement == null)) {
          attackTarget(context, aiObject, metadata, targetEval);
        }
        else if (p.pursuitStarted + metadata.aggressionHandler.maxPursuitTime > context.updateTime) {
          noTarget(context, aiObject, metadata);
        }
      }
      case a: Attacking => {
        val targetEval = evaluateTargets(context, players, aiObject, metadata, group, a.target);
        // aggro lost
        if (targetEval == null) {
          noTarget(context, aiObject, metadata);
        }
        // declare new attack if new target or target is not in view or we have stopped
        else if ((targetEval.target.id != a.target) || (targetEval.blockedTiles > 0) || (aiObject.movement == null)) {
          attackTarget(context, aiObject, metadata, targetEval);
        }
      }
    }

    metadata.lastUpdated = context.updateTime
  }

  def updateGroupRallyPoint(context: GameStateUpdateContext, players: Map[MapPosition, GameObject], group: AIGroup) {
    if (group.rallyPoint == null) {
      group.rallyPoint = players.first._1;
    }
    else {
      val closestPlayer = players.keySet.map(p => (p -> p.distanceTo(group.rallyPoint))).minBy(e => e._2);
      if (closestPlayer._2 < 40) {
        group.rallyPoint = closestPlayer._1;
      }
    }
    group.lastUpdated=context.updateTime;
  }

  def noTarget(context: GameStateUpdateContext, idle: GameObject, metadata: AIControlMetadata) {
    // TODO select next patrol point and set next update point will require access to context
    // group has target and we are far from it, go there
    if (context.state.getObjectPosition(idle.id).distanceTo(metadata.group.rallyPoint) > metadata.aggressionHandler.patrolLimit) {
      context.addUpdater(actionFactory.createMovementAction(idle, metadata.group.rallyPoint))
    }
  }

  def attackTarget(context: GameStateUpdateContext, attacker: GameObject, metadata: AIControlMetadata, targetEval: TargetEvaluation) {
    // can we see the target?
    if (targetEval.blockedTiles == 0) {
      metadata.state = Attacking(targetEval.target.id)
      context.addUpdater(actionFactory.createMeleeAction(attacker, targetEval.target.id));
    } // else pursue
    else {
      metadata.state = Pursuing(targetEval.target.id, System.nanoTime(), true)
      context.addUpdater(actionFactory.createMovementAction(attacker, context.state.getObjectPosition(targetEval.target.id)));
    }

    // alert the rest of the group that we got a new target
    if (metadata.group.groupTarget == null) {
      metadata.group.groupTarget = targetEval.target.id;
    }
  }

  def evaluateTargets(context: GameStateUpdateContext, players: Map[MapPosition, GameObject], monster: GameObject, metadata: AIControlMetadata, group: AIGroup, currentTarget: java.lang.Long): TargetEvaluation = {
    var target: TargetEvaluation = null;
    val currentPosition = context.state.getObjectPosition(monster.id);
    players.foreach(p => {
      val range = currentPosition.distanceTo(p._1);
      val isCurrentTarget = currentTarget != null && (currentTarget == p._2.id);
      val isGroupTarget = group.groupTarget != null && (group.groupTarget == p._2.id)
      val isLastAttacker = metadata.lastAttacker != null && (metadata.lastAttacker == p._2.id)

      // should we check at all?
      if (range < metadata.aggressionHandler.maxRange ||
        isCurrentTarget ||
        isGroupTarget ||
        isLastAttacker) {

        // get distance and calc blocked and free tiles. NOTE: this is most likely the performance bottleneck but i really like to avoid radius only triggers
        val (freeTiles, solidTiles) = {
          val solidsAndFress = buildLine(currentPosition.x, currentPosition.y, p._1.x, p._1.y).partition(t => context.state.getBackground(t.x, t.y).solid);
          (solidsAndFress._2.size, solidsAndFress._1.size)
        }

        val score = metadata.aggressionHandler.tileBonus(freeTiles, solidTiles)
        +(if (isCurrentTarget) metadata.aggressionHandler.currentTargetBonus else 0)
        +(if (isGroupTarget) metadata.aggressionHandler.groupTargetBonus else 0)
        +(if (isLastAttacker) metadata.aggressionHandler.latestAttackerBonus else 0)
        +metadata.aggressionHandler.objectBonus(p._2)

        // is this the new top target? 
        if (score > metadata.aggressionHandler.aggroLimit && (target == null || score > target.score)) {
          target = new TargetEvaluation(p._2, range, solidTiles, freeTiles, score);
        }
      }
    })
    return target;
  }

  def getPlayers(context: GameStateUpdateContext): Map[MapPosition, GameObject] = {
    val players = HashMap[MapPosition, GameObject]()
    context.state.getObjects(PlayerMetadata.getClass()).foreach(o => {
      players += (context.state.getObjectPosition(o.id) -> o)
    });
    return players;
  }
}