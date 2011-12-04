package se.faerie.sleep.client.view.graphics.light
import se.faerie.sleep.common.light.LineOfSightCalculator
import se.faerie.sleep.common.GameBackground._
import se.faerie.sleep.common.GraphicsCompressionHelper

class SimpleLightFactory(lightCaster: LineOfSightCalculator, maxRadius: Int) extends LightFactory with GraphicsCompressionHelper {


  def getLightSource(id: Int, seed: Long, time: Long = System.nanoTime()): LightSource = {
    val light = loadSphericalLight(id);
    return new SphericalLightSource(light._1 * light._2*50, light._1 * light._3*50, light._1 * light._4*50, maxRadius, lightCaster)
  }

  def getObjectAbsorption(id: Int, seed: Int, time: Long = System.nanoTime()): (Double, Double, Double) = {
    val lightAbs = loadGraphics(id)
    val redAbs = (lightAbs._2).asInstanceOf[Double] / Byte.MaxValue.asInstanceOf[Double];
    val greenAbs = (lightAbs._3).asInstanceOf[Double] / Byte.MaxValue.asInstanceOf[Double];
    val blueAbs = (lightAbs._4).asInstanceOf[Double] / Byte.MaxValue.asInstanceOf[Double];
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