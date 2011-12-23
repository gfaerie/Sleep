package se.faerie.sleep.server.ai

import se.faerie.sleep.server.ai.AIState._
import se.faerie.sleep.server.state.update.GameStateUpdateContext
import se.faerie.sleep.server.state.update.GameStateUpdater
import se.faerie.sleep.server.state.GameObjectMetadata

class AIController(val updateInterval: Long) extends GameStateUpdater {
  priority = 500;
  def update(context: GameStateUpdateContext): Unit = {
    assignOrders(context);
  }

  def assignOrders(context: GameStateUpdateContext) = {
    context.state.getObjects(GameObjectMetadata.AIControlled).foreach(o => {
      o match {
        case g: AIControlledGameObject => {
          if (g.lastCommand + updateInterval < context.updateTime) {
            assignObject(g, context);
            g.lastCommand= context.updateTime;
          }
        }
      }
    });
  }

  def assignObject(g: AIControlledGameObject, context: GameStateUpdateContext) {
    g.state match {
      case Sleeping => {}
      case m: Moving => {}
      case a: Attacking => {}
    }
  }

}