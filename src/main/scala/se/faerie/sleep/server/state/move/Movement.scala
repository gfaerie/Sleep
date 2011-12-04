package se.faerie.sleep.server.state.move
import se.faerie.sleep.common.MapPosition
import se.faerie.sleep.server.state.GameObject
import se.faerie.sleep.server.state.update.GameStateUpdateContext

trait Movement {
  def targetPosition(owner: GameObject, currentPosition : MapPosition, context : GameStateUpdateContext) : MapPosition
  def priority : Int
  def speed : Float
  def allowSkipTile : Boolean
  def done : Boolean
  
  // indicates we have move to current target
  def nextTarget : Unit
}