package se.faerie.sleep.client.view.graphics.light
import se.faerie.sleep.common.light.LineOfSightCalculator

class SphericalLightSource(red: Double, green: Double, blue: Double, maxRadius: Int, losCalculator: LineOfSightCalculator) extends LightSource {

  def distance(x: Int, y: Int): Double = x * x + y * y+1;

  def castLight(lightCallback: (Int, Int, Double, Double, Double) => Unit, blockFunction: (Int, Int) => Boolean, frameTime: Long) = {
    val processed = Array.ofDim[Boolean](2 * maxRadius + 1, 2 * maxRadius + 1);
    def losCallback = (x: Int, y: Int) => {
      val processedXPos = x + maxRadius;
      val processedYPos = y + maxRadius;
      if (!processed(processedXPos)(processedYPos)) {
        val distanceAttrition = 1 / (distance(x, y));
        lightCallback(x, y, red * distanceAttrition, green * distanceAttrition, blue * distanceAttrition);
        processed(processedXPos)(processedYPos) = true;
      }
    };
    losCalculator.calculateLos(losCallback, blockFunction, 0, 0);
  }
}