package se.faerie.sleep.simpletest
import java.net.InetSocketAddress
import java.nio.channels.DatagramChannel
import akka.actor.Actor._
import akka.actor.Scheduler._
import se.faerie.sleep.common.network._
import se.faerie.sleep.server.network._
import se.faerie.sleep.server.state._
import java.util.concurrent.TimeUnit
import se.faerie.sleep.common.network.NetworkProtocol._
import se.faerie.sleep.common.generate._
import se.faerie.sleep.server.state.update.GameStateUpdateActor
import se.faerie.sleep.server.state.move.MovementUpdater
import se.faerie.sleep.server.state.update._
import se.faerie.sleep.server.player._
import se.faerie.sleep.common.GraphicsCompressionHelper
import se.faerie.sleep.server.player.PlayerActionFactory
import se.faerie.sleep.server.player.actions._
import se.faerie.sleep.common.pathfinding.linear._
import se.faerie.sleep.common.pathfinding.astar._
import se.faerie.sleep.common.pathfinding._
import se.faerie.sleep.server.ServerCommands._

object SimpleServer {
  def main(args: Array[String]): Unit = {
    new SimpleServer().run;
  }
}

class SimpleServer() extends Runnable {
  val mapSize: Short = 500;
  val port = 11400;
  val mapId = System.currentTimeMillis;

  def run() = {
    val address = new InetSocketAddress("localhost", port);
    val channel = DatagramChannel.open()
    channel.socket.bind(address)
    val sender = actorOf(new NetworkSender(channel)).start

    val pathFinder = new CompositePathFinder(new LinearPathFinder :: new AStarPathFinder(new AStarManhattanHeuristic) :: Nil)
    val gameActor = actorOf(new GameStateUpdateActor).start
    val controller = actorOf(new ServerNetworkController(
        gameActor, 
        sender, 
        new SimplePlayerFactory, 
        new PlayerActionFactoryImpl(Set(new MovementAction(pathFinder), new CometBlitzAction, new BlackHoleAction)), 
        new SimpleGameStateFactory) with SimpleNetworkProtocol).start
    val networkReader = new Thread(new NetworkReader(channel, controller, 1000))
    networkReader.start
    schedule(gameActor, new GameStateUpdate, 10, 40, TimeUnit.MILLISECONDS)
    schedule(controller, new ResendData(100), 1000, 100, TimeUnit.MILLISECONDS)
    schedule(controller, new ClearData(20000), 1000, 2000, TimeUnit.MILLISECONDS)
    controller ! new ResetGame()
  }

}

