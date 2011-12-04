package se.faerie.sleep.client.view.graphics.light
import se.faerie.sleep.common.GameBackground._
import se.faerie.sleep.common.MapPosition

trait LightFactory {
  def getObjectAbsorption(id: Int, seed: Int, time: Long = System.nanoTime()): (Double, Double, Double);
  def getBackgroundAbsorption(background: GameBackground,seed: Int, time: Long = System.nanoTime()): (Double, Double, Double);
}