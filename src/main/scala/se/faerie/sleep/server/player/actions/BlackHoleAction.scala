package se.faerie.sleep.server.player.actions

import se.faerie.sleep.server.state.update.GameStateUpdateContext
import se.faerie.sleep.server.player.PlayerAction
import se.faerie.sleep.server.state.move.MovementHelper
import se.faerie.sleep.common.MapPosition
import se.faerie.sleep.server.state.GameObject
import se.faerie.sleep.server.state.update.GameStateUpdater
import se.faerie.sleep.server.player.PlayerAction
import se.faerie.sleep.common.TileGraphics
import se.faerie.sleep.common.TileLightSource
import scala.math._
import se.faerie.sleep.common.GameBackground
import se.faerie.sleep.server.state.collision.CollisionHandler
import se.faerie.sleep.common.GameBackground._
import se.faerie.sleep.server.state.update.helper.SingleUpdate

class BlackHoleAction extends PlayerAction with MovementHelper {
  val holeGraphics = new TileGraphics('*', 50.asInstanceOf[Byte], 50.asInstanceOf[Byte], 50.asInstanceOf[Byte])
  val holeCollisionHandler = new CollisionHandler {
    def isCollision(owner: GameObject, target: GameBackground, context: GameStateUpdateContext) = target.solid
    def isCollision(owner: GameObject, target: GameObject, context: GameStateUpdateContext) = false
    def onCollision(owner: GameObject, target: GameBackground, context: GameStateUpdateContext) {
      owner.hp = -1
    }
    def onCollision(owner: GameObject, target: GameObject, context: GameStateUpdateContext) { owner.hp = -1 }
  }

  class HoleCreator(ownerId: Long, angle: Double) extends SingleUpdate {
    def doUpdate(context: GameStateUpdateContext) = {
      for (i <- -1 to 1) {
        val owner = context.state.getObject(ownerId)
        val ownerPos = context.state.getObjectPosition(ownerId)
        val hole = new GameObject
        hole.graphicsId = (u) => holeGraphics
        hole.lightSource = (c) => new TileLightSource((-10.0 + 2.0 * sin(c.updateTime / 20000000.0)).asInstanceOf[Byte], (10.0 + 1.0 * sin(c.updateTime / 30000000.0)).asInstanceOf[Byte], (30.0 + 1.0 * sin(c.updateTime / 30000000.0)).asInstanceOf[Byte], (80.0 + 1.0 * sin(c.updateTime / 40000000.0)).asInstanceOf[Byte])
        hole.movement = getLineMovement(angle+i*Pi/12, ownerPos, 20)
        hole.collisionHandler = holeCollisionHandler
        context.state.addObject(ownerPos, hole)
      }
    }
  }

  val id: Long = 2

  def isValid(owner: GameObject) = true

  def doAction(owner: GameObject, target: MapPosition, context: GameStateUpdateContext) {
    val orginalPosition = context.state.getObjectPosition(owner.id)
    val angle = orginalPosition.angleTo(target)
    context.addUpdater(new HoleCreator(owner.id, angle))
  }

}