package se.faerie.sleep.server.ai

import se.faerie.sleep.server.state.update.GameStateUpdater
import se.faerie.sleep.server.state.GameObject
import se.faerie.sleep.common.MapPosition
import se.faerie.sleep.common.pathfinding.PathFinder
import se.faerie.sleep.server.state.update.helper.SingleUpdate
import se.faerie.sleep.server.state.update.GameStateUpdateContext
import se.faerie.sleep.server.state.move.HomingMovement
import se.faerie.sleep.server.state.move.PathMovement

class SimpleAIActionFactory(val pathFinder: PathFinder) extends AIActionFactory {

  def createMeleeAction(attacker: GameObject with AIControlMetadata, target: Long): GameStateUpdater = new SingleUpdate() {
    def doUpdate(context: GameStateUpdateContext) {
    	attacker.movement=new HomingMovement(target,0,20);
    }
  }

  def createMovementAction(pursuer: GameObject with AIControlMetadata, targetPosition: MapPosition): GameStateUpdater = new SingleUpdate() {
    def doUpdate(context: GameStateUpdateContext) {
    val start = context.state.getObjectPosition(pursuer.id)
    val costFunction: (Int, Int) => (Double) = (x, y) => if(pursuer.collisionHandler.isCollision(pursuer, context.state.getBackground(x, y), context)) -1 else 1;
    if (context.state.insideGame(targetPosition.x, targetPosition.y)) {
      val path = pathFinder.findPath(costFunction, start, targetPosition)
      pursuer.movement = new PathMovement(path, 0, 20)
    }
    }
  }
}