package se.faerie.sleep.server.player.actions
import se.faerie.sleep.server.player.PlayerAction
import se.faerie.sleep.common.MapPosition
import se.faerie.sleep.server.state.GameObject
import se.faerie.sleep.server.state.update.GameStateUpdateContext
import scala.math._
import se.faerie.sleep.server.state.move.FunctionMovement
import se.faerie.sleep.server.state.move.Movement
import se.faerie.sleep.server.state.update.GameStateUpdater
import se.faerie.sleep.common.GameBackground
import se.faerie.sleep.server.state.collision.CollisionHandler
import se.faerie.sleep.common.GameBackground._
import se.faerie.sleep.common.GraphicsCompressionHelper
import scala.util.Random
import se.faerie.sleep.common.GraphicsHelper
class BlitzAction extends PlayerAction with GraphicsCompressionHelper with GraphicsHelper {

  val random = new Random

  val tailCollisionHandler = new CollisionHandler {
    def isCollision(owner: GameObject, target: GameBackground, context: GameStateUpdateContext) = !target.passable
    def isCollision(owner: GameObject, target: GameObject, context: GameStateUpdateContext) = false
    def onCollision(owner: GameObject, target: GameBackground, context: GameStateUpdateContext) {
      owner.hp = -1
    }
    def onCollision(owner: GameObject, target: GameObject, context: GameStateUpdateContext) { owner.hp = -1 }
  }

  class TailCreator(ownerId: Long, originalMovement: Movement, angle: Double) extends GameStateUpdater {

    def update(context: GameStateUpdateContext) = {
      val owner = context.state.getObject(ownerId)
      val ownerPos = context.state.getObjectPosition(ownerId)
      
        for (i <- 0 to 100) {
          val tailGraphics = storeGraphics('*', 0, 0, Byte.MaxValue)
          val tail = new GameObject

          val randAngle = random.nextDouble * 2*Pi
          val randLength = random.nextInt(3) + 1
          val xAdd = (cos(randAngle + angle) * randLength).toInt;
          val yAdd = (sin(randAngle + angle) * randLength).toInt;
          val spawnPosition = new MapPosition(ownerPos.x + xAdd, ownerPos.y + yAdd)

          tail.collisionHandler = tailCollisionHandler;
          tail.movement = getMovement(angle, spawnPosition, random.nextInt(10) + 66)
          tail.graphicsId = (_) => tailGraphics

          context.state.addObject(spawnPosition, tail)
        }

        context.removeUpdater(this)
      
    }
    def priority: Long = 5;
  }

  val id: Long = 3

  def isValid(owner: GameObject) = true

  def doAction(owner: GameObject, target: MapPosition, context: GameStateUpdateContext) {
    val orginalPosition = context.state.getObjectPosition(owner.id)
    val angle = orginalPosition.angleTo(target)
    val movement = getMovement(angle, orginalPosition, 75)
    owner.movement = movement
    context.addUpdater(new TailCreator(owner.id, movement, angle))
  }

  private def getMovement(angle: Double, start: MapPosition, speed: Float): Movement = {
    val xFrac = cos(angle)
    val yFrac = sin(angle)
    val moveFunction: (Int) => (Int, Int) = (t) => {
      val xAdd = (xFrac * t).toInt
      val yAdd = (yFrac * t).toInt
      (xAdd + start.x, yAdd + start.y)
    }

    return new FunctionMovement(moveFunction, 1, speed)
  }

}