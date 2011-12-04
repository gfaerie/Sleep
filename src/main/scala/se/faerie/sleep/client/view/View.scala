package se.faerie.sleep.client.view
import se.faerie.sleep.client.view.graphics.GraphicsState
import akka.actor.ActorRef
import se.faerie.sleep.common.GameBackground._
import java.awt.Color
import se.faerie.sleep.client.view.graphics.MinimapState

trait View {
  var playerActionHandler : ActorRef = null;
  def showChat(author : String, message : String)
  def showLog(message : String)
  def showGraphics(graphics : GraphicsState)
  def showMinimap(map : MinimapState)
}