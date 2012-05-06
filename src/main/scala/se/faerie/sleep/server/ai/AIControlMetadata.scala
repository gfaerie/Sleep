package se.faerie.sleep.server.ai
import se.faerie.sleep.server.state.GameObjectMetadata
import se.faerie.sleep.server.state.AddionalGameObjectData
import se.faerie.sleep.server.state.GameObject

trait AIData extends AddionalGameObjectData {
  self: GameObject => 
  var group: AIGroup = null;
  var aggressionHandler: AggressionHandler = null;
  var state: AIState = null;
  var lastAttacker: java.lang.Long = null;
  var lastUpdated: Long = Long.MinValue;
  var speed: Float = 1;
}