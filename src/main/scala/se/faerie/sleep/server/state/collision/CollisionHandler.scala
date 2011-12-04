package se.faerie.sleep.server.state.collision
import se.faerie.sleep.server.state.GameObject
import se.faerie.sleep.common.GameBackground._
import se.faerie.sleep.server.state.update.GameStateUpdateContext
import se.faerie.sleep.server.state.update.GameStateUpdater

trait CollisionHandler {
  def isCollision(owner : GameObject,  target : GameBackground, context : GameStateUpdateContext) : Boolean
  def isCollision(owner : GameObject , target : GameObject, context : GameStateUpdateContext) : Boolean
  def onCollision(owner : GameObject,  target : GameBackground, context : GameStateUpdateContext)
  def onCollision(owner : GameObject , target : GameObject, context : GameStateUpdateContext)
}

object CollisionHandler {
  val none = new CollisionHandler{
     def isCollision(owner : GameObject,  target : GameBackground, context : GameStateUpdateContext) = false
     def isCollision(owner : GameObject , target : GameObject, context : GameStateUpdateContext) = false
     def onCollision(owner : GameObject,  target : GameBackground, context : GameStateUpdateContext){}
     def onCollision(owner : GameObject , target : GameObject, context : GameStateUpdateContext){}
  }
}