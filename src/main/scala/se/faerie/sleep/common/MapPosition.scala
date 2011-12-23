package se.faerie.sleep.common

import scala.math._

/**
 *
 * Defines a position on the map
 *
 * @author Faerie
 *
 */
class MapPosition(val x: Int, val y: Int) extends Ordered[MapPosition] {
  def compare(that: MapPosition): Int = x * x + y * y - that.x * that.x - that.y * that.y;

  override def equals(other: Any): Boolean =
    other match {
      case other: MapPosition => x == other.x && y == other.y
      case _ => false
    }

  override def hashCode: Int = 41 * (41 + x) + y

  override def toString = x + "," + y

  def translate(xDiff: Int, yDiff: Int) = new MapPosition(x + xDiff, y + yDiff)
  def translate(position: MapPosition) = new MapPosition(x + position.x, y + position.y)

  def angleTo(other: MapPosition): Double = {
    val xDiff: Double = other.x - x;
    val yDiff: Double = other.y - y;
    val quota = yDiff / xDiff
    if (xDiff > 0) {
      return atan(quota)
    } else if (yDiff >= 0 && xDiff < 0) {
      return atan(quota) + Pi
    } else if (yDiff < 0 && xDiff < 0) {
      return atan(quota) - Pi
    } else if (yDiff > 0 && xDiff == 0) {
      return Pi / 2
    } else {
      return -Pi / 2
    }
  }

  def distanceTo(other: MapPosition): Double = {
    val xDiff: Double = other.x - x;
    val yDiff: Double = other.y - y;
    return sqrt(xDiff * xDiff + yDiff * yDiff);
  }
  
   def differnece(other: MapPosition): MapPosition = {
    val xDiff = other.x - x;
    val yDiff = other.y - y;
    return new MapPosition(xDiff,yDiff);
  }
}