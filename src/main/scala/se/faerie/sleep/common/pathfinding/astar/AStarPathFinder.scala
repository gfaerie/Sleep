package se.faerie.sleep.common.pathfinding.astar

import collection.mutable.ListBuffer
import collection.mutable.Set
import collection.mutable.Map
import collection.mutable.PriorityQueue
import se.faerie.sleep.common.MapPosition
import se.faerie.sleep.common.pathfinding._
import scala.math._

class AStarPathFinder(heuristic: AStarHeuristic) extends PathFinder {
  private val sqrtTwo = sqrt(2);
  private val maxCheck = 200;
  private val neighbourNumbers = 1 :: 0 :: -1 :: Nil
  private val neighbours = for (x <- neighbourNumbers; y <- neighbourNumbers; if (x != 0 || y != 0)) yield { new MapPosition(x, y) }
  
  private implicit val ordering = new Ordering[AStarMapNode]{
    def compare(x: AStarMapNode, y: AStarMapNode): Int = {
      val f = x.heuristic + x.cost;
      val of = y.heuristic + y.cost;
      if (f < of) {
        return 1;
      } else if (f > of) {
        return -1;
      } else {
        return 0;
      }
    }
  };

  def findPath(costFunction: (Int, Int) => Double, start: MapPosition, end: MapPosition): List[MapPosition] = {
    val closedSet = Set[AStarMapNode]()
    val openQueue = new PriorityQueue[AStarMapNode]()
    if(costFunction(start.x,start.y)<0){
    	openQueue += new AStarMapNode(null, start.x, start.y, 0, 0, 0)
    }
    
    while (!openQueue.isEmpty) {
      val current = openQueue.dequeue
      if (current.x == end.x && current.y == end.y) {
        val returnList = new ListBuffer[MapPosition]()
        return addToList(returnList, current).reverse.toList
      }
      neighbours.foreach(pos =>
        {
          val cost: Double = (if (pos.x == 0 || pos.y == 0) 1 else sqrtTwo)*costFunction(current.x + pos.x, current.y + pos.y);
          val node = new AStarMapNode(current, current.x + pos.x, current.y + pos.y, 0, 0, cost + current.cost)
          if (!(cost<0 || closedSet.contains(node))) {
            node.heuristic = heuristic.cost(node.x, node.y, end.x, end.y)
            openQueue += node
          }
        })
      closedSet += current

      if (closedSet.size > maxCheck) {
        return emptyList;
      }
    }
    return emptyList;
  }

  private def addToList(currentPositions: ListBuffer[MapPosition], node: AStarMapNode): ListBuffer[MapPosition] = {
    currentPositions += new MapPosition(node.x, node.y);
    if (node.parent != null) {
      return addToList(currentPositions, node.parent)
    } else {
      return currentPositions
    }
  }

}

