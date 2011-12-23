package se.faerie.sleep.server.state.update
import se.faerie.sleep.server.state.priority.PriorityLevel

trait GameStateUpdater extends PriorityLevel{

  def update(context: GameStateUpdateContext)
}

object GameStateUpdater {
  val none = new GameStateUpdater {
    def update(context: GameStateUpdateContext) {context.removeUpdater(this)}
  }

  def multiple(updaters: Traversable[GameStateUpdater], priority: Long = 0) = {
    new GameStateUpdater() {
      def update(context: GameStateUpdateContext) {
        updaters.foreach(u => u.update(context))
      }
      this.priority=priority;
    }
  }
}