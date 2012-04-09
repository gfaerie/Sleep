package se.faerie.sleep.server.ai
import se.faerie.sleep.common.MapPosition
import se.faerie.sleep.server.state.move.Movement
import se.faerie.sleep.server.state.update.GameStateUpdater
import se.faerie.sleep.server.state.GameObject
import se.faerie.sleep.server.state.update.GameStateUpdateContext

trait AIActionFactory {
  def createMeleeAction(attacker : GameObject, target : Long) : GameStateUpdater;
  def createMovementAction(pursuer : GameObject, targetPosition : MapPosition) : GameStateUpdater;
}