package se.faerie.sleep.common.pathfinding.astar

trait AStarHeuristic {
    def cost(startX: Int,startY: Int,endX : Int, endY: Int): Double
}
