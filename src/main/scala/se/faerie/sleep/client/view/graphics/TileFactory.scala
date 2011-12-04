package se.faerie.sleep.client.view.graphics
import se.faerie.sleep.common.GameBackground._
import se.faerie.sleep.common.TileGraphics

trait TileFactory {

  def getObjectTile(data : TileGraphics, time : Long = System.nanoTime()) : String;
  def getBackgroundTile(background : GameBackground, time : Long= System.nanoTime()) : String;
}