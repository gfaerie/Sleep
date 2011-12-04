package se.faerie.sleep.common.pathfinding.linear

import se.faerie.sleep.common.MapPosition
import scala.collection.immutable.List
import se.faerie.sleep.common.GraphicsHelper
import se.faerie.sleep.common.pathfinding._

class LinearPathFinder extends PathFinder with GraphicsHelper {

  def findPath(blockFunction: (Int, Int) => Boolean, start: MapPosition, end: MapPosition): List[MapPosition] = {
    val path = buildLine(start.x, start.y, end.x, end.y);
    path.foreach(pos =>
      if (blockFunction(pos.x, pos.y)) {
         return emptyList;
      });
    return path;
  }
}