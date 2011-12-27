package se.faerie.sleep.common.pathfinding

import se.faerie.sleep.common.MapPosition;

trait PathFinder {
  protected val emptyList = scala.collection.immutable.List[MapPosition]();

  //costFunction < 0 means tile unpassable
  def findPath(costFunction: (Int, Int) => Double, start: MapPosition, end: MapPosition): List[MapPosition]
}
