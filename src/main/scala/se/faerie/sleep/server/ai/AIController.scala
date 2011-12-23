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

    group.lastUpdated = context.updateTime;
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