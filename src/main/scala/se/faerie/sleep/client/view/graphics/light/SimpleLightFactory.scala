package se.faerie.sleep.client.view.graphics.light
import se.faerie.sleep.common.light.LineOfSightCalculator
import se.faerie.sleep.common.GameBackground._
import se.faerie.sleep.common._
import se.faerie.sleep.common.GraphicsCompressionHelper

class SimpleLightFactory(lightCaster: LineOfSightCalculator, maxRadius: Int) extends LightFactory  {


  def getLightSource(data: TileLightSource, seed: Long, time: Long = System.nanoTime()): LightSource = {
    return new SphericalLightSource(data.strength * data.red*50, data.green * data.strength*50, data.strength * data.blue*50, maxRadius, lightCaster)
  }

  def getObjectAbsorption(data: TileGraphics, seed: Int, time: Long = System.nanoTime()): (Double, Double, Double) = {
    val redAbs = (data.red).asInstanceOf[Double] / Byte.MaxValue.asInstanceOf[Double];
    val greenAbs = (data.green).asInstanceOf[Double] / Byte.MaxValue.asInstanceOf[Double];
    val blueAbs = (data.blue).asInstanceOf[Double] / Byte.MaxValue.asInstanceOf[Double];
    return (redAbs, greenAbs, blueAbs)
  }

  def getBackgroundAbsorption(background: GameBackground, seed: Int, time: Long = System.nanoTime()): (Double, Double, Double) = {
    background match {
      case Wall => {
        return (0.4 + (seed % 9) * 0.03, 0.4 + (seed % 9) * 0.03, 0.4 + (seed % 9) * 0.03);
      }
      case Water => {
        return (0.990 + (seed % 7) * 0.001, 0.990 + (seed % 8) * 0.001, 0.2 + (seed % 9) * 0.01);
      }
      case Floor => {
        return (0.4 + (seed % 7) * 0.01, 0.4 + (seed % 7) * 0.01, 0.4 + (seed % 7) * 0.01);
      }
      case _ => return (1, 1, 1);
    }
  }

}