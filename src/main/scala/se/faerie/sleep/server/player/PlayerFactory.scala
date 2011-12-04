package se.faerie.sleep.server.player
import se.faerie.sleep.server.state.GameObject

trait PlayerFactory {
	def createPlayer(id : Long, name : String): GameObject;
}