package se.faerie.sleep.simpletest
import java.awt.Font
import java.net.InetSocketAddress
import java.nio.channels.DatagramChannel
import java.util.concurrent.TimeUnit
import akka.actor.Actor._
import akka.actor.Scheduler
import se.faerie.sleep.client.PlayerCommands._
import se.faerie.sleep.client.view._
import se.faerie.sleep.common.network.NetworkProtocol._
import se.faerie.sleep.client.network._
import se.faerie.sleep.common.network._
import se.faerie.sleep.client.view.graphics.light._
import se.faerie.sleep.common.light._
import se.faerie.sleep.client.view.graphics._
import se.faerie.sleep.common.generate._

object SimpleClient {
  def main(args: Array[String]): Unit = {
    new SimpleClient().run;
  }
}

class SimpleClient() extends Runnable {
  val screenSize: Short = 775;
  val serverport = 11400;
  val clientport = 11401;
  val font = new Font("SanSerif", Font.PLAIN, 9);

  def run() = {
    val address = new InetSocketAddress("localhost", serverport);
    val channel = DatagramChannel.open()
    channel.socket.bind(new InetSocketAddress(clientport))
    val sender = actorOf(new NetworkSender(channel)).start
    val renderingInfo = new GraphicsRenderingInfo(screenSize, screenSize, 0, 0, font)
    val view = new SimpleView(renderingInfo)
    val renderer = actorOf(new GraphicsBuilderActor(new SimpleGraphicsBuilder(new SimpleLightFactory(new LineOfSightCalculatorImpl(50), 50), new SimpleTileFactory(), new NormalColorBlender, new WhiteLightColorBlender, 40, 40), new BlobMapBuilder())).start
    val networkController = actorOf(new ClientNetworkController(sender, renderer, view) with SimpleNetworkProtocol).start
    val readerRunnable = new NetworkReader(channel, networkController, 1000)
    val networkReader = new Thread(readerRunnable)
    networkReader.start
    Scheduler.schedule(networkController, new ResendData(100), 1000, 100, TimeUnit.MILLISECONDS)
    Scheduler.schedule(networkController, new ClearData(3000), 1000, 3000, TimeUnit.MILLISECONDS)

    Runtime.getRuntime().addShutdownHook(new Thread() {
      override def start = {
        view.showLog("Shutting down actors and threads");
        sender.stop()
        renderer.stop()
        networkController.stop()
        readerRunnable.running = false
        view.showLog("Closing network channel");
        channel.close()
      }
    });
  }

}