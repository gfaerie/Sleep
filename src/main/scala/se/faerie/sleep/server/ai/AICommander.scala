package se.faerie.sleep.server.ai

import se.faerie.sleep.server.state.update.helper.PeriodicUpdate
import se.faerie.sleep.server.state.update.GameStateUpdateContext

/**
 * 
 * Assigns orders to ai controlled objects (sets targets and patrol points)
 * 
 */
class AICommander(updateInterval : Long) extends PeriodicUpdate(updateInterval) {

  def doUpdate(context: GameStateUpdateContext) {
    // check for groups that are far away from players, move some of them closer to the players or between the players and the goal
    
    // check for players that have strayed from the pack assign them as group targets?
    
    
  
  }

}