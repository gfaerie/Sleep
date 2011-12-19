package se.faerie.sleep.server.state.move
import scala.math._
import se.faerie.sleep.common.MapPosition

trait MovementHelper {

   def getLineMovement(angle: Double, start: MapPosition, speed: Float): Movement = {
    val xFrac = cos(angle)
    val yFrac = sin(angle)
    val moveFunction: (Int) => (Int, Int) = (t) => {
      val xAdd = (xFrac * t).toInt
      val yAdd = (yFrac * t).toInt
      (xAdd + start.x, yAdd + start.y)
    }

    return new FunctionMovement(moveFunction, 1, speed);
  }
  
}