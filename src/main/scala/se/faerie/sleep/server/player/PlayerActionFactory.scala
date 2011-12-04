package se.faerie.sleep.server.player

import se.faerie.sleep.server.state.GameObject
import se.faerie.sleep.server.state.update.GameStateUpdater
import se.faerie.sleep.common.MapPosition

trait PlayerActionFactory {
  def createAction(objectId: Long, actionId : Long, target : MapPosition) : GameStateUpdater;
}