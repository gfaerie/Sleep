package se.faerie.sleep.server.ai
import se.faerie.sleep.server.state.GameObjectMetadata
import se.faerie.sleep.common.MapPosition

sealed trait AIState{
}

object AIState {
  case object Sleeping extends AIState
  case class Attacking(val target : Long) extends AIState 
  case class Moving(val target : MapPosition) extends AIState 
}