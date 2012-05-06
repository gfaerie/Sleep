package se.faerie.sleep.simpletest
import se.faerie.sleep.server.state.GameStateFactory
import se.faerie.sleep.server.ServerCommands.GameStateData
import akka.actor.ActorRef
import se.faerie.sleep.common.generate.BlobMapBuilder
import se.faerie.sleep.server.state.SimpleGameState
import se.faerie.sleep.server.state.move.MovementUpdater
import se.faerie.sleep.server.state.update.PlayerUpdater
import se.faerie.sleep.server.state.update.GameObjectReaper
import se.faerie.sleep.server.state.rules.LastManStandingWithGhosts
import se.faerie.sleep.server.ai.AIData
import se.faerie.sleep.server.player.PlayerData
import se.faerie.sleep.server.state.AddionalGameObjectData

class SimpleGameStateFactory extends GameStateFactory {

  private val mapBuilder = new BlobMapBuilder();

  def createNewGameState(controller: ActorRef): GameStateData = {
    val mapId = System.nanoTime
    val mixinsToIndex : Set[Class[_ <: AddionalGameObjectData]] =  Set(classOf[AIData],classOf[PlayerData]);
    new GameStateData(
      new SimpleGameState(mapId, new BlobMapBuilder().buildBackground(mapId), 50, mixinsToIndex), List(
        new MovementUpdater,
        new PlayerUpdater(40, controller),
        new GameObjectReaper,
        new LastManStandingWithGhosts(controller)))
  }

}