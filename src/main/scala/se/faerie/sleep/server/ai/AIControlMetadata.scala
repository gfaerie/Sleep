package se.faerie.sleep.server.ai
import se.faerie.sleep.server.state.GameObjectMetadata

case class AIControlMetadata(val group : AIGroup,val aggressionHandler : AggressionHandler) extends GameObjectMetadata{
	var state : AIState= null; 
	var lastAttacker : java.lang.Long =  null;
	var lastUpdated : Long = Long.MinValue;
}