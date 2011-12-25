package se.faerie.sleep.server.ai
import se.faerie.sleep.server.state.move.Movement
import se.faerie.sleep.server.state.update.GameStateUpdater
import se.faerie.sleep.common.MapPosition

trait AIActionFactory {
  def createMeleeAction(pursuer : Long, target : Long, meleeAttackId : Long) : GameStateUpdater;
  def createPursuitAction(pursuer : Long, targetPosition : MapPosition, meleeAttackId : Long) : GameStateUpdater;
}