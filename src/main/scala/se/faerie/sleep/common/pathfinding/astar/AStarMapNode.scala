package se.faerie.sleep.common.pathfinding.astar

class AStarMapNode (
  var parent: AStarMapNode,
  val x: Int,
  val y: Int,
  var depth: Int, 
  var heuristic: Double,
  var cost: Double){
 
  override def equals(other: Any): Boolean =
    other match {
      case other: AStarMapNode => x == other.x && y == other.y
      case _ => false
    }

  override def hashCode: Int = 41 * (41 + x) + y
	
  override def toString = "x,y:"+x+","+y+" heuricstic,cost:"+heuristic+","+cost
  
  
} 
