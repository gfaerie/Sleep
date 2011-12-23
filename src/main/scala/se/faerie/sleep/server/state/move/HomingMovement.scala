package se.faerie.sleep.server.state.move

import se.faerie.sleep.server.state.update.GameStateUpdateContext
import se.faerie.sleep.server.state.GameObject
import se.faerie.sleep.common.MapPosition
import scala.math._

class HomingMovement(target: Long, priority: Int, speed: Float) extends Movement {
  
  def targetPosition(owner: GameObject, currentPosition: MapPosition, context: GameStateUpdateContext): MapPosition = { 
		val targetPos = context.state.getObjectPosition(target)
		val angle=currentPosition.angleTo(targetPos)
		val y = sin(angle)
		val x = cos(angle)
		val xPos : Int = if(abs(x)>=HomingMovement.border) signum(x).toInt else 0
		val yPos : Int  = if(abs(y)>=HomingMovement.border) signum(y).toInt else 0
		return currentPosition.translate(xPos,yPos)
  }

  def priority(): Int = priority

  def speed(): Float = speed

  def allowSkipTile(): Boolean = { false }

  def done(): Boolean = { false }

  def nextTarget(): Unit = {}

}

object HomingMovement{
  val border = sin(Pi/6);
}