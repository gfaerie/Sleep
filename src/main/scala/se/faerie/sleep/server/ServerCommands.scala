package se.faerie.sleep.server
import se.faerie.sleep.server.state.GameState
import se.faerie.sleep.server.state.update.GameStateUpdater

object ServerCommands {
	trait ServerCommand
	case class Shutdown(timeout : Long) extends ServerCommand
  	case class ResetGame() extends ServerCommand
  	case class GameStateData(state : GameState, initialUpdaters : Traversable[GameStateUpdater]) extends ServerCommand
  	case class GameStateUpdate() extends ServerCommand
}