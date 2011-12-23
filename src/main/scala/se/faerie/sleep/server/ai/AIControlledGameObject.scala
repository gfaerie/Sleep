package se.faerie.sleep.server.ai

import se.faerie.sleep.server.state.GameObject
import se.faerie.sleep.server.state.GameObjectMetadata;

class AIControlledGameObject(val group : AIGroup, val additionalMetadata : Set[GameObjectMetadata] = null) extends GameObject(AIControlledGameObject.getMetadata(additionalMetadata)) {
	var state : AIState= AIState.Sleeping; 
	var lastAttacker : java.lang.Long =  null;
	; 
}

object AIControlledGameObject{
  private val baseMetaData: Set[GameObjectMetadata] = Set(GameObjectMetadata.AIControlled);
  def getMetadata(additionalMetadata : Set[GameObjectMetadata]) : Set[GameObjectMetadata] = {
    if(additionalMetadata==null||additionalMetadata.size==0){
      return baseMetaData;
    }
    else{
      return additionalMetadata++baseMetaData;
    }
  }
}

