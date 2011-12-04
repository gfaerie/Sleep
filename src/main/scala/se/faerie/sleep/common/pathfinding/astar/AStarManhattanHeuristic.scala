package se.faerie.sleep.common.pathfinding.astar

import scala.math._

class AStarManhattanHeuristic extends AStarHeuristic{
  
    def cost(startX: Int,startY: Int,endX : Int, endY: Int): Double = 
      abs(endY-startY)+abs(endX-startX) 
}
