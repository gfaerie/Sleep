package se.faerie.sleep.server.state.update.helper
import se.faerie.sleep.server.state.update.GameStateUpdater
import se.faerie.sleep.server.state.update.GameStateUpdateContext

abstract class SingleUpdate extends GameStateUpdater{
		
   def update(context : GameStateUpdateContext) = {
     doUpdate(context)
     context.removeUpdater(this)
   } 
   
   def doUpdate(context : GameStateUpdateContext)
  
}