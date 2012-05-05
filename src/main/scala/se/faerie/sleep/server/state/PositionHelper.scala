package se.faerie.sleep.server.state
import scala.util.Random

import se.faerie.sleep.common.MapPosition
import update.GameStateUpdateContext

trait PositionHelper {
  private val random = new Random();

  def randomFreePosition(context: GameStateUpdateContext): MapPosition = {
    // add check so we dont loop forever, also recursive solution probably not the best
    val position = new MapPosition(random.nextInt(context.state.width), random.nextInt(context.state.height));
    if (isPositionFree(context, position)) {
      return position;
    }
    else {
      return randomFreePosition(context);
    }
  }
  
  
  def randomPosition(context: GameStateUpdateContext) = new MapPosition(random.nextInt(context.state.width), random.nextInt(context.state.height));

  def isPositionFree(context: GameStateUpdateContext, position: MapPosition) = {
    context.state.getBackground(position.x, position.y).passable && context.state.getObjectsAtPosition(position) == null;
  }

}