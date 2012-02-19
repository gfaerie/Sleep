package se.faerie.sleep.server.state.rules

import se.faerie.sleep.server.state.update.GameStateUpdater
import se.faerie.sleep.server.state.update.GameStateUpdateContext
import se.faerie.sleep.server.state.GameObjectMetadata._
import se.faerie.sleep.server.ServerCommands._
import akka.actor.ActorRef
import se.faerie.sleep.server.player.PlayerMetadata

class LastManStandingWithGhosts(controller: ActorRef) extends GameStateUpdater {

  def update(context: GameStateUpdateContext): Unit = {
    val players = context.state.getObjects(PlayerMetadata.getClass)
    // make ghost of players with hp < 0
    players.filter(_.hp < 0).foreach(p =>
      {
        p.dynamicMetadata += Ghost
        p.dynamicMetadata -= Solid
      })

    // if only one player is left standing restart
    if (players.size >= 2 && players.filter(_.hp > 0).size < 2) {
      controller ! ResetGame
    }

  }
  priority = 1;
}