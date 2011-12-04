package se.faerie.sleep.server.player
import se.faerie.sleep.common.MapPosition
import se.faerie.sleep.server.state.update.GameStateUpdateContext
import se.faerie.sleep.server.state.update.GameStateUpdater

class PlayerActionFactoryImpl(actions: Traversable[PlayerAction]) extends PlayerActionFactory {

  private val actionMap = actions.map { (a) => (a.id, a) }.toMap

  private class ActionUpdater(objectId: Long, action: PlayerAction, target: MapPosition) extends GameStateUpdater {
    def update(context: GameStateUpdateContext) = {
      try {
        val owner = context.state.getObject(objectId)
        if (action.isValid(owner)) {
          action.doAction(owner, target, context)
        }
      } catch {
        case e: Exception => e.printStackTrace
      } finally {
        context.removeUpdater(this)
      }
    }
    def priority: Long = 100;
  }

  def createAction(objectId: Long, actionId: Long, target: MapPosition): GameStateUpdater = {
    actionMap.get(actionId) match {
      case Some(a) => return new ActionUpdater(objectId, a, target)
      case None => return null;
    }

  }

}