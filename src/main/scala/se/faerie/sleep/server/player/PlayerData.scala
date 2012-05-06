package se.faerie.sleep.server.player
import se.faerie.sleep.server.state.GameObjectMetadata
import se.faerie.sleep.server.state.AddionalGameObjectData
import se.faerie.sleep.server.state.GameObject

trait PlayerData extends AddionalGameObjectData{
  self: GameObject => 
}