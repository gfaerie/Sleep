package se.faerie.sleep.server.player
import se.faerie.sleep.server.state.update.GameStateUpdater
import se.faerie.sleep.server.state.GameObject
import se.faerie.sleep.server.state.update.GameStateUpdateContext
import se.faerie.sleep.common.MapPosition

trait PlayerAction {
  def id : Long;
  def isValid(owner : GameObject) : Boolean;
  def doAction(owner : GameObject , targte : MapPosition , context : GameStateUpdateContext);
}