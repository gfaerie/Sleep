package se.faerie.sleep.client.view.graphics
import scala.util.Random
import scala.math._
class GhostColorBlender extends ColorBlender {

  def blend(colors: (Double, Double, Double), abs: (Double, Double, Double)): Int = {

    val red = max(0, min(colors._1 * (1 - abs._1), 255))
    val green = max(0, min(colors._2 * (1 - abs._2), 255))
    val blue = max(0, min(colors._3 * (1 - abs._3), 255))

    val maxColor = 255 - min(min(red, green), blue)
    val minColor = min(200, 255 - max(max(red, green), blue))

    return ((255 & 0xFF) << 24) |
      ((minColor.toInt & 0xFF) << 16) |
      ((minColor.toInt & 0xFF) << 8) |
      ((maxColor.toInt & 0xFF) << 0);
  }

}