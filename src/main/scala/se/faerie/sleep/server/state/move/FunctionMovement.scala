package se.faerie.sleep.server.state.move
import se.faerie.sleep.common.MapPosition
import se.faerie.sleep.server.state.update.GameStateUpdateContext
import se.faerie.sleep.server.state.GameObject

class FunctionMovement(paramFunc: (Int) => (Int,Int), priority: Int, speed: Float) extends Movement {

  private var index = 0;

  def targetPosition(owner : GameObject,currentPosition: MapPosition, context: GameStateUpdateContext): MapPosition = {
    val pos = paramFunc(index)
    return new MapPosition(pos._1,pos._2)
  }

  def priority(): Int = priority

  def speed(): Float = speed

  def nextTarget(): Unit = index += 1

  def allowSkipTile = false
  
  def done = false

}