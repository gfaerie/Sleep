package se.faerie.sleep.client.view.graphics
import se.faerie.sleep.common.MapPosition;
import se.faerie.sleep.common.network.NetworkProtocol.GameUpdate
import se.faerie.sleep.common.GameBackground._

trait GraphicsBuilder {

  def buildGraphics(state : GameUpdate, backGround: Array[Array[GameBackground]])  : GraphicsState;
  def buildMinimap(backGround: Array[Array[GameBackground]])  : MinimapState;

}