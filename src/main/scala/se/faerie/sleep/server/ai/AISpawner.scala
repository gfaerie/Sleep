package se.faerie.sleep.server.ai
import se.faerie.sleep.server.ai.AIMonsterFactory
import se.faerie.sleep.server.player.PlayerMetadata
import se.faerie.sleep.server.state.update.helper.PeriodicUpdate
import se.faerie.sleep.server.state.update.GameStateUpdateContext
import se.faerie.sleep.server.state.PositionHelper
import se.faerie.sleep.server.state.update.helper.SingleUpdate
import se.faerie.sleep.common.MapPosition
import se.faerie.sleep.server.state.GameObject
import se.faerie.sleep.server.state.update.helper.DelayedUpdate
import se.faerie.sleep.server.ai.AIControlMetadata


/**
 *
 * Spawns new ai units
 *
 */
class AISpawner(difficulty: Int, updateInterval: Long, monsterFactory: AIMonsterFactory) extends PeriodicUpdate(updateInterval) with PositionHelper {
  
 class MonsterAdder(delay : Long, monster : GameObject) extends DelayedUpdate(delay) {
    def doUpdate(context: GameStateUpdateContext) = {
      val position = spawnPosition(context)
      context.state.addObject(position,monster)
    }
  }
  
  def doUpdate(context: GameStateUpdateContext) {
    val points = spawnPoints(context);
    spawnGroups(context, points);
  }

  def spawnPoints(context: GameStateUpdateContext): Double = {
    val nrPlayers = context.state.getObjects(PlayerMetadata.getClass()).size
    val minutes = (context.updateTime - context.startTime) / (1000000000 * 60)
    val nrMonsters: Double = context.state.getObjects(AIControlMetadata.getClass()).size
    return difficulty * nrPlayers * (1 + minutes / 30.0) - nrMonsters;
  }

  def spawnGroups(context: GameStateUpdateContext, points: Double) {
    val group = monsterFactory.createGroup(points)
    group.foreach(m => context.addUpdater(new MonsterAdder(context.updateTime+(m.id%10)*100000000, m)));
  }

  def spawnPosition(context: GameStateUpdateContext) = randomFreePosition(context);

}