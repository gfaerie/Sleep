package se.faerie.sleep.client.view.graphics

import scala.math._

class WhiteLightColorBlender extends ColorBlender {

  def blend(colors: (Double, Double, Double), abs: (Double, Double, Double)): Int = {

    var red = max(0, colors._1 * (1 - abs._1))
    var green = max(0, colors._2 * (1 - abs._2))
    var blue = max(0, colors._3 * (1 - abs._3))

    var maxColor = max(max(red, green), blue)
    if (maxColor > 255) {
      red = red * 255 / maxColor
      green = green * 255 / maxColor
      blue = blue * 255 / maxColor
    }

    return ((255 & 0xFF) << 24) |
      ((red.toInt & 0xFF) << 16) |
      ((green.toInt & 0xFF) << 8) |
      ((blue.toInt & 0xFF) << 0);
  }

}  