package se.faerie.sleep.common.pathfinding
import se.faerie.sleep.common.MapPosition

class CompositePathFinder(finders: List[PathFinder]) extends PathFinder {

  def findPath(costFunction: (Int, Int) => Double, start: MapPosition, end: MapPosition): List[MapPosition] = {
    if (costFunction(start.x, start.y)<0) {
      return emptyList;
    } else if (costFunction(end.x, end.y)<0) {
      return emptyList;
    }
    for (finder <- finders) {
      val path = finder.findPath(costFunction, start, end);
      if (!path.isEmpty) {
        return path;
      }
    }
    return emptyList;
  }
}