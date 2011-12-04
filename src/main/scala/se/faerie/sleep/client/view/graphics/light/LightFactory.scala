package se.faerie.sleep.client.view.graphics.light
import se.faerie.sleep.common.GameBackground._
import se.faerie.sleep.common._

trait LightFactory {  def getLightSource(data : TileLightSource, seed: Long, time: Long = System.nanoTime()): LightSource;
  def getObjectAbsorption(data : TileGraphics, seed: Int, time: Long = System.nanoTime()): (Double, Double, Double);
  def getBackgroundAbsorption(background: GameBackground,seed: Int, time: Long = System.nanoTime()): (Double, Double, Double);
}