package se.faerie.sleep.client.view.graphics
import akka.actor.Actor
import se.faerie.sleep.common.network.NetworkProtocol._
import se.faerie.sleep.common.GameBackground._
import se.faerie.sleep.common.generate.MapBuilder

class GraphicsBuilderActor(builder: GraphicsBuilder, mapBuilder: MapBuilder) extends Actor {

  private var backGround: Array[Array[GameBackground]] = null;
  private var mapId : Long=Long.MinValue
  
  def receive = {
    case g: GameUpdate => {
      if (backGround == null || mapId != g.mapId) {
        mapId= g.mapId;
        backGround = mapBuilder.buildBackground(g.mapId)
        self.reply(builder.buildMinimap(backGround))
      }
      self.reply(builder.buildGraphics(g, backGround))
    }
  }

}