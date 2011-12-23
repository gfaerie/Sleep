package se.faerie.sleep.server.state
import java.util.concurrent.atomic.AtomicLong
import se.faerie.sleep.server.state.collision.CollisionHandler
import se.faerie.sleep.server.state.move.Movement
import se.faerie.sleep.server.state.update.GameStateUpdateContext
import se.faerie.sleep.common.TileLightSource
import se.faerie.sleep.common.TileGraphics

object GameObject{
	val idCounter = new AtomicLong(Long.MinValue);
	val emptySet : Set[GameObjectMetadata] = Set();
}

class GameObject(val staticMetadata : Set[GameObjectMetadata]=GameObject.emptySet, val id : Long = GameObject.idCounter.incrementAndGet){
	def this() = this(Set.empty,GameObject.idCounter.incrementAndGet)
	var origin : Long = id;
	var layer : Byte = 0;
	var team : Byte = 0;
	var hp : Float = 0;
	var dynamicMetadata : Set[GameObjectMetadata] = Set.empty;
	var lightSource : (GameStateUpdateContext) => (TileLightSource) = null;
	var graphicsId: (GameStateUpdateContext) => (TileGraphics) = null;
	var moveFraction : Float = 0.0f;
	var movement : Movement = null;
	var collisionHandler : CollisionHandler = CollisionHandler.none;
}

