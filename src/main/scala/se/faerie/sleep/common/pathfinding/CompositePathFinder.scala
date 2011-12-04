package se.faerie.sleep.common.pathfinding
import se.faerie.sleep.common.MapPosition

class CompositePathFinder(finders: List[PathFinder]) extends PathFinder {

  def findPath(blockFunction: (Int, Int) => Boolean, start: MapPosition, end: MapPosition): List[MapPosition] = {
    if (blockFunction(start.x, start.y)) {
      return emptyList;
    } else if (blockFunction(end.x, end.y)) {
      return emptyList;
    }
    for (finder <- finders) {
      val path = finder.findPath(blockFunction, start, end);
      if (!path.isEmpty) {
        return path;
      }
    }
    return emptyList;
  }
}