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

    val state = context.state;
    // for each monster in group
    groupContents.filter(g => (g.lastUpdated + updateInterval) < context.updateTime).foreach(m => {
      m.state match {
        case Sleeping => {
          val target = evaluateTargets(context, players, m);
          if (target != null) {
            attack(context, m, target.id);
            group.groupTarget = target.id;
          } else if (group.groupTarget != null) {
            attack(context, m, group.groupTarget);

          }
        }
        case p: Patrolling => {
          val target = evaluateTargets(context, players, m);
          if (target != null) {
            attack(context, m, target.id);
            group.groupTarget = target.id;
          } else if (group.groupTarget != null) {
            attack(context, m, group.groupTarget);
          } else if (target.movement == null) {
            // plot path to new position
          }
        }
        case p: Pursuing => {
          // is my target still alive?

          // can I see my target?

          // have I been attacked by another target different from the on im pursuing consider switching
        }
        case a: Attacking => {
          // is my target still alive?

          // can I see my target?

          // have I been attacked by another target different from the on im attacking consider switching

        }
      }
      m.lastUpdated = context.updateTime
    })
  }

  def targetInView(context: GameStateUpdateContext, attacker: GameObject, target: Long): Boolean = {
    return false;
  }

  def attack(context: GameStateUpdateContext, attacker: GameObject, target: Long) {
    // can we see the target?

    // put on pursue path to engage in melee

    // if we can't see the player go to its last known position
  }

  def evaluateTargets(context: GameStateUpdateContext, players: HashMap[MapPosition, GameObject], monster: AIControlledGameObject): GameObject = {

    // check players within range
    
    // draw a line to players in range
    
    // calculate score from line, unblocked =1, blocked =10
    
    // if score < border return
    
    // if no valid targets return null
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
          returnMap.addBinding(g.group, g);
        }
      }
    });
    return returnMap;
  }
}