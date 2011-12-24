package se.faerie.sleep.server.ai

import se.faerie.sleep.server.ai.AIState._
import se.faerie.sleep.server.state.update.GameStateUpdateContext
import se.faerie.sleep.server.state.update.GameStateUpdater
import se.faerie.sleep.server.state.GameObjectMetadata
import se.faerie.sleep.common.MapPosition
import se.faerie.sleep.server.state.GameObject
import scala.collection.mutable.HashMap
import scala.collection.mutable.MultiMap

class AIController(val updateInterval: Long) extends GameStateUpdater {

  class PlayerStatus(val openTiles: Int, val closedTiles: Int, val range: Double, playerId: Long) {

  }

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

    var groupTarget: java.lang.Long = null;
    val state = context.state;
    // for each monster in group
    groupContents.foreach(m => {
      m.state match {
        case Sleeping => {
          if (groupTarget != null) {
            if (targetInView(context, m, groupTarget)) {
              attack(context, m, groupTarget);
            }
          } else {
            val target = evaluateTargets(context, players, m);
            if (target != null) {
              attack(context, m, target.id);
              groupTarget = target.id;
            }
          }
        }
        case p: Patrolling => {
          val target = evaluateTargets(context, players, m);
          if (target != null) {
            attack(context, m, target.id);
            groupTarget = target.id;
          }

          //check if we reached the end of the patrol
        }
        case p: Pursuing => {

        }
        case a: Attacking => {}
      }
    })

    // if not attacked check for nearby players to engage

    // if attacking player check if target switch

    // if we take a new target alert the rest of the group

    // can we see the target?

    // put on pursue path to engage in melee

    // if we can't see the player go to its last known position

    group.lastUpdated = context.updateTime;
  }

  def targetInView(context: GameStateUpdateContext, attacker: GameObject, target: Long): Boolean ={
	  return false;
  }

  def attack(context: GameStateUpdateContext, attacker: GameObject, target: Long) {

  }

  def evaluateTargets(context: GameStateUpdateContext, players: HashMap[MapPosition, GameObject], monster: AIControlledGameObject): GameObject = {

    return null;
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
          if (g.group.lastUpdated + updateInterval < context.updateTime) {
            returnMap.addBinding(g.group, g);
          }
        }
      }
    });
    return returnMap;
  }
}