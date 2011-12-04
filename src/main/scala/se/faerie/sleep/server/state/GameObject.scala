package se.faerie.sleep.server.state
import java.util.concurrent.atomic.AtomicLong
import se.faerie.sleep.server.state.collision.CollisionHandler
import se.faerie.sleep.server.state.move.Movement
import se.faerie.sleep.server.state.update.GameStateUpdateContext

object GameObject{
	val idCounter = new AtomicLong(Long.MinValue);
}

class GameObject(val staticMetadata : Set[GameObjectMetadata], val id : Long = GameObject.idCounter.incrementAndGet){
	def this() = this(Set.empty,GameObject.idCounter.incrementAndGet)
	var layer : Byte = 0;
	var team : Byte = 0;
	var hp : Float = 0;
	var dynamicMetadata : Set[GameObjectMetadata] = Set.empty;
	var lightSource : (GameStateUpdateContext) => (Int) = null;
	var graphicsId: (GameStateUpdateContext) => (Int) = null;
	var moveFraction : Float = 0.0f;
	var movement : Movement = null;
	var collisionHandler : CollisionHandler = CollisionHandler.none;
}

