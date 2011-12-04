package se.faerie.sleep.common.pathfinding

import se.faerie.sleep.common.MapPosition;

trait PathFinder {
  protected val emptyList = scala.collection.immutable.List[MapPosition]();

  def findPath(blockFunction: (Int, Int) => Boolean, start: MapPosition, end: MapPosition): List[MapPosition]
}
