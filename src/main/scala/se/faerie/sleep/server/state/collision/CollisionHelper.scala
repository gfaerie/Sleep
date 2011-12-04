package se.faerie.sleep.server.state.collision
import se.faerie.sleep.common.MapPosition
import se.faerie.sleep.server.state.GameObject
import se.faerie.sleep.server.state.update.GameStateUpdateContext

trait CollisionHelper {

  def checkCollisions(gameObject: GameObject, context: GameStateUpdateContext, position: MapPosition): Boolean = {

    val background = context.state.getBackground(position.x, position.y)
    val occupants = context.state.getObjectsAtPosition(position).toIterator

    // did we collide with background
    var stop = gameObject.collisionHandler.isCollision(gameObject, background, context)
    if (stop) {
      gameObject.collisionHandler.onCollision(gameObject, background, context)
    }
    // loop until we checked all occupants or we collided with something
    while (!stop && occupants.hasNext) {
      val occupant = occupants.next

      // did we collide
      stop = gameObject.collisionHandler.isCollision(gameObject, occupant, context)
      if (stop) {
        gameObject.collisionHandler.onCollision(gameObject, occupant, context)
      }

      // did object collide with us? ( we don't stop for that but there may be other consequences)
      if (occupant.collisionHandler.isCollision(occupant, gameObject, context)) {
        occupant.collisionHandler.onCollision(occupant, gameObject, context)
      }
    }
    return stop;
  }
}