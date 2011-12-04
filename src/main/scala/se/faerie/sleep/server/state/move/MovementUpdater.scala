package se.faerie.sleep.server.state.move

import se.faerie.sleep.server.state.update.GameStateUpdater
import se.faerie.sleep.server.state.update.GameStateUpdateContext
import se.faerie.sleep.server.state.GameObject
import se.faerie.sleep.server.state.InvalidGameStateException
import scala.math._
import se.faerie.sleep.common.MapPosition
import se.faerie.sleep.server.state.collision.CollisionHelper

class MovementUpdater extends GameStateUpdater with CollisionHelper {

  def update(context: GameStateUpdateContext) = {
    for (o <- context.state.getAllObjects) {
      if (o.movement != null) {
        o.moveFraction += (o.movement.speed * (context.updateTime-context.lastUpdateTime).asInstanceOf[Double]/1000000000.0).asInstanceOf[Float]
        handleObjectMovement(o, context)
      } else if (o.moveFraction > 0) {
        o.moveFraction = 0
      }
    }
  }

  private def handleObjectMovement(o: GameObject, context: GameStateUpdateContext) {

    // check if movement is done
    if (o.movement == null) {
      return
    } else if (o.movement.done) {
      o.movement = null
      return
    }

    val oldPosition = context.state.getObjectPosition(o.id)
    val nextPosition = o.movement.targetPosition(o, oldPosition, context)

    // object does not want to move right now
    if (nextPosition == null) {
      return
    }

    // check distance
    val xDiff = nextPosition.x - oldPosition.x
    val yDiff = nextPosition.y - oldPosition.y
    var distanceSquare: Float = xDiff * xDiff + yDiff * yDiff

    try {

      // object already at target
      if (distanceSquare == 0) {
        o.movement.nextTarget
        handleObjectMovement(o, context)
      } // object has moved too much from the original position
      else if (distanceSquare > 2 && !o.movement.allowSkipTile) {
        o.movement = null;
      } // object can still follow the path, move along the path recursively until we can't move anymore
      else {
        var fractionRequiered: Float = if (distanceSquare > 1) sqrt(distanceSquare).asInstanceOf[Float] else 1
        if (o.moveFraction > fractionRequiered) {
          o.moveFraction -= fractionRequiered;
          if (!checkCollisions(o, context, nextPosition)) {
            context.state.moveObject(o.id, nextPosition)
            handleObjectMovement(o, context)
          }
        }
      }
      // problem with movement, stop movement
    } catch {
      case e: InvalidGameStateException => o.movement = null;
    }
  }

  def priority(): Long = { 0L }

}