package se.faerie.sleep.server.state.update
import se.faerie.sleep.server.state.GameObjectMetadata._
import se.faerie.sleep.server.player.PlayerMetadata

class GameObjectReaper extends GameStateUpdater {

  // remove objects with hp < 0
  def update(context: GameStateUpdateContext): Unit = {
    context.state.getAllObjects.filter(o => o.hp < 0 && !o.staticMetadata.exists(o => o.getClass().equals(PlayerMetadata.getClass))).foreach(i =>{
      // non-players get removed
        context.state.removeObject(i.id)
    });
  }

  // run almost last
  priority =2;

}