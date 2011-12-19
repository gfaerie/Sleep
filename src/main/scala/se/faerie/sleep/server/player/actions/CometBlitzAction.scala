package se.faerie.sleep.server.player.actions
import se.faerie.sleep.server.player.PlayerAction
import se.faerie.sleep.common._
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
class CometBlitzAction extends PlayerAction with GraphicsCompressionHelper with GraphicsHelper {

  val random = new Random
  val light: (GameStateUpdateContext) => (TileLightSource) = (c) => new TileLightSource((40.0+4.0*sin(c.updateTime/20000000.0)).asInstanceOf[Byte], (10.0+1.0*sin(c.updateTime/30000000.0)).asInstanceOf[Byte], (30.0+1.0*sin(c.updateTime/30000000.0)).asInstanceOf[Byte], (80.0+1.0*sin(c.updateTime/40000000.0)).asInstanceOf[Byte])

  val tailCollisionHandler = new CollisionHandler {
    def isCollision(owner: GameObject, target: GameBackground, context: GameStateUpdateContext) = target.solid
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
      
        for (i <- 0 to 50) {
          val tailGraphics = new TileGraphics('*', Byte.MaxValue, (i*2).asInstanceOf[Byte], 0)
          val tail = new GameObject

          val addAngle = random.nextDouble()*2*Pi;
          val length = 3.0*random.nextDouble();
          val xAdd = (cos(angle+addAngle) * length).toInt;
          val yAdd = (sin(angle+addAngle) * length).toInt;
          val spawnPosition = new MapPosition(ownerPos.x + xAdd, ownerPos.y + yAdd)

          tail.collisionHandler = tailCollisionHandler;
          tail.movement = getMovement(angle, spawnPosition, 67+i/5)
          tail.graphicsId = (u) => new TileGraphics('*', Byte.MaxValue, (i*2+10).asInstanceOf[Byte], 0)

          if(i%10==0){
               tail.lightSource = (c) => new TileLightSource((7.0+2.0*sin(c.updateTime/20000000.0)).asInstanceOf[Byte], (10.0+1.0*sin(c.updateTime/30000000.0)).asInstanceOf[Byte], (30.0+1.0*sin(c.updateTime/30000000.0)).asInstanceOf[Byte], (80.0+1.0*sin(c.updateTime/40000000.0)).asInstanceOf[Byte])
          }
          
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

    return new FunctionMovement(moveFunction, 1, speed){
      override def allowSkipTile = true;
    }
  }

}