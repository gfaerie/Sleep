package se.faerie.sleep.simpletest

import se.faerie.sleep.common.GameBackground._
import se.faerie.sleep.common._
import se.faerie.sleep.common.GraphicsCompressionHelper
import se.faerie.sleep.server.player.PlayerFactory
import se.faerie.sleep.server.state.GameObjectMetadata._
import se.faerie.sleep.server.state.collision.CollisionHandler
import se.faerie.sleep.server.state.update.GameStateUpdateContext
import se.faerie.sleep.server.state.GameObject
import scala.math._

class SimplePlayerFactory extends PlayerFactory with GraphicsCompressionHelper {
  val playerCollisionHandler = new CollisionHandler {
    def isCollision(owner: GameObject, target: GameBackground, context: GameStateUpdateContext) = !target.passable
    def isCollision(owner: GameObject, target: GameObject, context: GameStateUpdateContext) = target.staticMetadata.contains(Solid)
    def onCollision(owner: GameObject, target: GameBackground, context: GameStateUpdateContext) { owner.movement = null }
    def onCollision(owner: GameObject, target: GameObject, context: GameStateUpdateContext) { owner.movement = null }
  }

  val graphics = new TileGraphics('@', 0.asInstanceOf[Byte], 126.asInstanceOf[Byte], 126.asInstanceOf[Byte])

  val playerGraphics: (GameStateUpdateContext) => (TileGraphics) = (_) => graphics
  val playerLight: (GameStateUpdateContext) => (TileLightSource) = (c) => new TileLightSource((20.0+4.0*sin(c.updateTime/20000000.0)).asInstanceOf[Byte], (80.0+1.0*sin(c.updateTime/30000000.0)).asInstanceOf[Byte], (30.0+1.0*sin(c.updateTime/30000000.0)).asInstanceOf[Byte], (10.0+1.0*sin(c.updateTime/40000000.0)).asInstanceOf[Byte])

  def createPlayer(id: Long, name: String): GameObject = {
    val player = new GameObject(Set(Player)) {
      override def toString = name
    }
    player.dynamicMetadata= Set(Solid)
    player.collisionHandler = playerCollisionHandler
    player.layer = 1
    player.graphicsId = playerGraphics
    player.lightSource = playerLight
    return player;
  }

}