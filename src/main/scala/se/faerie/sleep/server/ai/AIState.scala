package se.faerie.sleep.server.ai
import se.faerie.sleep.server.state.GameObjectMetadata
import se.faerie.sleep.common.MapPosition

sealed trait AIState{
}

object AIState {
  case class Attacking(val target : Long) extends AIState
  case class Pursuing(val target : Long, val pursuitStarted : Long, val abortable : Boolean) extends AIState
  case class Patrolling() extends AIState 
}