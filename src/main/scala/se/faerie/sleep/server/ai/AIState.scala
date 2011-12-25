package se.faerie.sleep.server.ai
import se.faerie.sleep.server.state.GameObjectMetadata
import se.faerie.sleep.common.MapPosition

sealed trait AIState{
}

object AIState {
  case object Sleeping extends AIState
  case class Attacking(val target : Long) extends AIState
  case class Pursuing(val target : Long) extends AIState
  case class Patrolling(val positions : List[MapPosition]) extends AIState 
}