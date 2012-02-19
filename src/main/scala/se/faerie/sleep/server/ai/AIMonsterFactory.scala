package se.faerie.sleep.server.ai
import se.faerie.sleep.server.state.GameObject

trait AIMonsterFactory {
  def createGroup(value: Double): Traversable[GameObject];
}