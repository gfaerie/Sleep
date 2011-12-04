package se.faerie.sleep.server.state.update

trait GameStateUpdater {

  def update(context: GameStateUpdateContext)
  def priority: Long;
}

object GameStateUpdater {
  val none = new GameStateUpdater {
    def update(context: GameStateUpdateContext) {context.removeUpdater(this)}
    def priority: Long = 0;
  }

  def multiple(updaters: Traversable[GameStateUpdater], priortity: Long = 0) = {
    new GameStateUpdater() {
      def update(context: GameStateUpdateContext) {
        updaters.foreach(u => u.update(context))
      }
      def priority: Long = Long.MaxValue;
    }
  }
}