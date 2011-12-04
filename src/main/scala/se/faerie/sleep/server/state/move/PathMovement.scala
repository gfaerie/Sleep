package se.faerie.sleep.server.state.move

import se.faerie.sleep.common.MapPosition
import se.faerie.sleep.server.state.update.GameStateUpdateContext
import se.faerie.sleep.server.state.GameObject

class PathMovement(path: List[MapPosition], priority: Int, speed: Float) extends Movement {

  private var index = 0;

  def targetPosition(owner : GameObject ,currentPosition: MapPosition, context: GameStateUpdateContext): MapPosition = if (index < path.size) path(index) else null
 
  def priority() = priority

  def speed() = speed

  def nextTarget = index += 1

  def allowSkipTile = false
  
  def done = index >= path.size
  
}