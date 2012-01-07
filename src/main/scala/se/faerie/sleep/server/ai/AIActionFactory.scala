package se.faerie.sleep.server.ai
import se.faerie.sleep.common.MapPosition
import se.faerie.sleep.server.state.move.Movement
import se.faerie.sleep.server.state.update.GameStateUpdater
import se.faerie.sleep.server.state.GameObject
import se.faerie.sleep.server.state.update.GameStateUpdateContext

trait AIActionFactory {
  def createMeleeAction(attacker : GameObject, target : Long, meleeAttackId : Long) : GameStateUpdater;
  def createPursuitAction(pursuer : GameObject, targetPosition : MapPosition, meleeAttackId : Long) : GameStateUpdater;
  def createPatrolAction(patroller : GameObject, group: AIGroup) : GameStateUpdater;
}