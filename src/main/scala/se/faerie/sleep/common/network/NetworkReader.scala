package se.faerie.sleep.common.network
import se.faerie.sleep.common.network.NetworkProtocol.NetworkData
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel
import scala.actors.Actor
import akka.actor.ActorRef
import java.nio.channels.AsynchronousCloseException

class NetworkReader(datagramChannel: DatagramChannel, controller: ActorRef, maxPacketSize: Int) extends Runnable {
  var running = true;
  def run {
    while (datagramChannel.isOpen && running) {
      try {
        val buffer = ByteBuffer.allocate(maxPacketSize)
        val sender = datagramChannel.receive(buffer); buffer.flip
        if(running){
        	controller ! new NetworkData(buffer, sender)
        }
      } catch { // expected during shutdown  
        case a: AsynchronousCloseException => { if (running) { a.printStackTrace } }
        case e: Exception =>
          {
            e.printStackTrace
          }
      }
    }
  }
}
