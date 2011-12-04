package se.faerie.sleep.server.state.update
import scala.collection.mutable.ListBuffer
import se.faerie.sleep.server.state.GameObjectMetadata._
import se.faerie.sleep.server.state.collision.CollisionHandler

class GameObjectReaper extends GameStateUpdater {

  // remove objects with hp < 0
  def update(context: GameStateUpdateContext): Unit = {
    context.state.getAllObjects.filter(o => o.hp < 0).foreach(i =>
      // non-players get removed
      if (!i.staticMetadata.contains(Player)) {
        context.state.removeObject(i.id)
      })
  }

  // run almost last
  def priority(): Long = { 2 }

}