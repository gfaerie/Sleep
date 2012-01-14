package se.faerie.sleep.server.ai
import se.faerie.sleep.server.state.update.GameStateUpdater
import se.faerie.sleep.server.state.update.helper.PeriodicUpdate
import se.faerie.sleep.server.state.update.GameStateUpdateContext

/**
 * 
 * Spawns new ai units
 * 
 */
class AISpawner(updateInterval: Long) extends PeriodicUpdate(updateInterval) {

  def doUpdate(context: GameStateUpdateContext) {

    // check if we should spawn units (check existing units and players)
    
    // find position to spawn units (out of sight of players but not too far)
    
    // select type of group
    
    // let factory create group
    
    // add group contents to gamestate
    
  }

}