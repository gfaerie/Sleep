package se.faerie.sleep.server.state
import se.faerie.sleep.server.ServerCommands.GameStateData
import akka.actor.ActorRef

trait GameStateFactory {

  // TODO add settings
  def createNewGameState(controller : ActorRef): GameStateData;
  
}