package se.faerie.sleep.server.state.update

import scala.collection.mutable.ListBuffer
import se.faerie.sleep.common._
import se.faerie.sleep.server.state.GameObject
import se.faerie.sleep.common.network.NetworkProtocol._
import akka.actor.ActorRef
import se.faerie.sleep.server.state.update._
import se.faerie.sleep.server.state.GameObjectMetadata
import se.faerie.sleep.common.ViewModes


class PlayerUpdater(radius: Int, controller: ActorRef) extends GameStateUpdater {

  def update(context: GameStateUpdateContext) = {
    val players = context.state.getObjects(GameObjectMetadata.Player);
    for (player <- players) {
      val time = System.nanoTime();
      val position = context.state.getObjectPosition(player.id)
      val objects = context.state.getObjects(position.x - radius, position.y - radius, position.x + radius, position.y + radius);
      val syncDraw = new ListBuffer[(MapPosition, TileGraphics)]();
      val syncLights = new ListBuffer[(MapPosition, TileLightSource)]();
      for (o <- objects) {
        var drawObject: GameObject = null;
        for (d <- o._2) {
          if (d.graphicsId!= null && (drawObject == null || drawObject.layer < d.layer)) {
            drawObject = d
          }
          if (d.lightSource != null) {
            val light = (o._1, d.lightSource(context))
            syncLights += light
          }
        }
        val draw = (o._1, drawObject.graphicsId(context))
        syncDraw += draw
      }
      controller ! new GameUpdate(player.id, context.state.id,if (player.dynamicMetadata.contains(GameObjectMetadata.Ghost)) ViewModes.Ghost else ViewModes.Normal, position, syncDraw, syncLights);
      context.getLogs.foreach(l => new ChatMessage(player.id,l))
    }
  }

  // run after everything else
  def priority(): Long = { 1 }

}