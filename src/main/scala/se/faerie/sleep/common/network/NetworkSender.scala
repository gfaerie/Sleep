package se.faerie.sleep.common.network
import se.faerie.sleep.common.network.NetworkProtocol._
import java.nio.channels.DatagramChannel
import scala.collection.mutable.Map;
import akka.actor.Actor

class NetworkSender(channel: DatagramChannel) extends Actor {

  def receive = {
        case n: NetworkData => {
          sendData(n)
        }
        case c : ConfirmableNetworkData =>{
          c.data.foreach(d => sendData(d))
          self.reply(true)
        }
  }

  def sendData(n: NetworkData) {
    try {
      n.data.flip
      val sent = channel.send(n.data, n.address)
    } catch {
      case e: Exception =>
        {
          e.printStackTrace
        }
    }
  }
}