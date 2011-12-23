package se.faerie.sleep.server.state.update.helper

import se.faerie.sleep.server.state.update.GameStateUpdater
import se.faerie.sleep.server.state.update.GameStateUpdateContext

abstract class PeriodicUpdate(period: Long) extends GameStateUpdater {

  var lastTriggered = Long.MinValue;

  def update(context: GameStateUpdateContext): Unit = {
    if (lastTriggered + period < context.updateTime) {
      doUpdate(context);
      lastTriggered = context.updateTime;
    }
  }

  def doUpdate(context: GameStateUpdateContext)

}