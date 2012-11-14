package se.faerie.sleep.common.network
import java.nio.channels.AsynchronousCloseException
import java.nio.channels.DatagramChannel
import java.nio.ByteBuffer

import akka.actor.actorRef2Scala
import akka.actor.ActorRef
import se.faerie.sleep.common.network.NetworkProtocol.NetworkData

class NetworkReader(datagramChannel: DatagramChannel, controller: ActorRef, maxPacketSize: Int) extends Runnable {
  
  @volatile
  var running = true;
  
  def run {
    while (datagramChannel.isOpen && running) {
      try {
        val buffer = ByteBuffer.allocate(maxPacketSize)
        val sender = datagramChannel.receive(buffer) 
        buffer.flip
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
