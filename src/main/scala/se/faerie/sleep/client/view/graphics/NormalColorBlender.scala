package se.faerie.sleep.client.view.graphics

import scala.math._

class NormalColorBlender extends ColorBlender {

  def blend(colors: (Double, Double, Double), abs: (Double, Double, Double)): Int = {
    cappedBlend(colors, abs)
  }

  private def cappedBlend(colors: (Double, Double, Double), abs: (Double, Double, Double)): Int = {
    val red = max(0, min(colors._1 * (1 - abs._1), 255))
    val green = max(0, min(colors._2 * (1 - abs._2), 255))
    val blue = max(0, min(colors._3 * (1 - abs._3), 255))
    return ((255 & 0xFF) << 24) |
      ((red.toInt & 0xFF) << 16) |
      ((green.toInt & 0xFF) << 8) |
      ((blue.toInt & 0xFF) << 0);
  }
}