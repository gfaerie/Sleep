package se.faerie.sleep.client.view.graphics
import se.faerie.sleep.common.GameBackground._

trait TileFactory {

  def getObjectTile(id : Int, time : Long = System.nanoTime()) : String;
  def getBackgroundTile(background : GameBackground, time : Long= System.nanoTime()) : String;
}