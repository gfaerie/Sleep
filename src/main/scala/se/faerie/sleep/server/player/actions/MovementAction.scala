package se.faerie.sleep.server.player.actions

import se.faerie.sleep.common.pathfinding.PathFinder
import se.faerie.sleep.common.MapPosition
import se.faerie.sleep.server.player.PlayerAction
import se.faerie.sleep.server.state.move.PathMovement
import se.faerie.sleep.server.state.update.GameStateUpdateContext
import se.faerie.sleep.server.state.GameObject

class MovementAction(pathFinder: PathFinder) extends PlayerAction {

  val id: Long = 1

  // you can always move if noone has a higher priority movement
  def isValid(owner: GameObject) = owner.movement == null || owner.movement.priority <= 0

  def doAction(owner: GameObject, target: MapPosition, context: GameStateUpdateContext) {
    val start = context.state.getObjectPosition(owner.id)
    val blockFunction: (Int, Int) => (Boolean) = (x, y) => owner.collisionHandler.isCollision(owner, context.state.getBackground(x, y), context)
    if (context.state.insideGame(target.x, target.y)) {
      val path = pathFinder.findPath(blockFunction, start, target)
      owner.movement = new PathMovement(path, 0, getSpeed(owner))
    }
  }

  def getSpeed(owner: GameObject) = 25

}