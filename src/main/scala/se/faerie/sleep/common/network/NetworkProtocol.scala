package se.faerie.sleep.common.network
import java.util.concurrent.atomic.AtomicLong
import java.nio.ByteBuffer
import se.faerie.sleep.common.MapPosition
import java.net.SocketAddress
import se.faerie.sleep.common.ViewModes.ViewMode

trait NetworkProtocol {
  def encode(message: NetworkProtocol.NetworkMessage): ByteBuffer;
  def decode(message: ByteBuffer): NetworkProtocol.NetworkMessage;
}

object NetworkProtocol {
  private val idCounter = new AtomicLong();

  // messages actually send over network
  abstract class NetworkMessage(val id: Long)
  case class Connect(name: String, val msgId: Long = idCounter.incrementAndGet) extends NetworkMessage(msgId)
  abstract class SessionMessage(id: Long, val sessionId: Long) extends NetworkMessage(id)
  case class NetworkMessageAck(ackMsgId: Long, val sId: Long) extends SessionMessage(-2, sId)
  case class Disconnect(sId: Long, reconnect : Boolean= false, msgId: Long = idCounter.incrementAndGet) extends SessionMessage(msgId, sId)
  case class ChatMessage(sId: Long, msgId: Long = idCounter.incrementAndGet, message : String, author : String) extends SessionMessage(msgId, sId){
    def this(sId: Long, message : String) = this(sId,idCounter.incrementAndGet,message,"");
    def this(sId: Long, message : String, author : String) = this(sId,idCounter.incrementAndGet,message,author);
  }
  case class GameUpdate(sId: Long,mapId: Long,viewMode : ViewMode,  centralPosition: MapPosition, objects: Traversable[(MapPosition, Int)], lights: Traversable[(MapPosition, Int)]) extends SessionMessage(-1, sId)
  case class ActionCommand(sId: Long, actionId: Long, targetPosition: MapPosition, msgId: Long = idCounter.incrementAndGet) extends SessionMessage(msgId, sId)

  // raw data representation
  class NetworkData(val data: ByteBuffer, val address: SocketAddress) {
    def duplicate = new NetworkData(data.duplicate, address)
  }
  
  // used when sender has to reply that data has been send during shutdown
  class ConfirmableNetworkData(val data : Seq[NetworkData]) {
    def this(data : NetworkData ) = this(data :: Nil)
  }
  
  // command classes
  case class ClearData(val age: Long)
  case class ResendData(val age: Long)
}