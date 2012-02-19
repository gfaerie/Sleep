package se.faerie.sleep.server.state.update
import se.faerie.sleep.server.state.GameState

trait GameStateUpdateContext {
  	def addUpdater(updater: GameStateUpdater);
	def removeUpdater(updater: GameStateUpdater);
	def state : GameState;
	def getLogs : Traversable[String];
	def addLog(log : String);
	def updateTime : Long;
	def lastUpdateTime : Long;
	def startTime : Long;

}