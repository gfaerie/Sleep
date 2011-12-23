package se.faerie.sleep.server.state.update.helper

import se.faerie.sleep.server.state.update.GameStateUpdater
import se.faerie.sleep.server.state.update.GameStateUpdateContext

abstract class DelayedUpdate(triggerTime: Long) extends SingleUpdate {

  override def update(context: GameStateUpdateContext): Unit = {
    if (context.updateTime > triggerTime) {
      super.update(context);
    }
  }
}