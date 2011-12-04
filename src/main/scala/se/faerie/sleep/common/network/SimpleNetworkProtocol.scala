package se.faerie.sleep.common.network
import java.nio.ByteBuffer
import scala.collection.mutable.ListBuffer
import se.faerie.sleep.common._
import se.faerie.sleep.common.network.NetworkProtocol._
import se.faerie.sleep.common.network.SimpleNetworkProtocol._
import java.util.Arrays
import se.faerie.sleep.common.ViewModes._

// TODO viewmode compression sucks
trait SimpleNetworkProtocol extends NetworkProtocol {

  def encode(message: NetworkProtocol.NetworkMessage): ByteBuffer = {
    message match {
      case Connect(name, msgId) => {
        val data = ByteBuffer.allocate(19 + 2 + 2 * name.length);
        data.putShort(PROTOCOL_ID);
        data.put(CONNECT_ID);
        data.putLong(msgId);
        data.putShort(name.length.asInstanceOf[Short]);
        for (i <- 0 until name.length()) {
          data.putChar(name.charAt(i))
        }
        return data;
      }
      case Disconnect(id, reconnect, msgId) => {
        val data = ByteBuffer.allocate(20);
        data.putShort(PROTOCOL_ID);
        data.put(DISCONNECT_ID);
        data.putLong(id);
        data.put((if (reconnect) 1 else 0).asInstanceOf[Byte]);
        data.putLong(msgId);
        return data;
      }
      case GameUpdate(id, mapId, viewMode, centralPosition, objects, lights) => {
        val length = 2 + 1 + 1 + 8 + 8 + 2 + 2 + 2 + objects.size * 6 + 2 + lights.size * 6;
        val data = ByteBuffer.allocate(length);
        data.putShort(PROTOCOL_ID);
        data.put(GAME_UPDATE_ID);
        data.putLong(id);
        data.putLong(mapId);
        data.put((if (viewMode == Normal) 1 else 0).asInstanceOf[Byte])
        data.putShort(centralPosition.x.toShort);
        data.putShort(centralPosition.y.toShort);

        data.putShort(objects.size.toShort);
        for (o <- objects) {
          data.put((o._1.x - centralPosition.x).toByte);
          data.put((o._1.y - centralPosition.y).toByte);
          data.put(o._2.char.asInstanceOf[Byte]);
          data.put(o._2.red);
          data.put(o._2.green);
          data.put(o._2.blue);
        }

        data.putShort(lights.size.toShort);
        for (o <- lights) {
          data.put((o._1.x - centralPosition.x).toByte);
          data.put((o._1.y - centralPosition.y).toByte);
          data.put(o._2.strength);
          data.put(o._2.red);
          data.put(o._2.green);
          data.put(o._2.blue);
        }
        return data;
      }
      case ActionCommand(id, actionId, position, msgId) => {
        val data = ByteBuffer.allocate(31);
        data.putShort(PROTOCOL_ID);
        data.put(ACTION_COMMAND_ID);
        data.putLong(id);
        data.putLong(actionId);
        data.putShort(position.x.toShort);
        data.putShort(position.y.toShort);
        data.putLong(msgId);
        return data;
      }
      case c: ChatMessage => {
        val data = ByteBuffer.allocate(2 + 1 + 8 + 8 + 2 + 2 * c.message.length + 2 + 2 * c.author.length);
        data.putShort(PROTOCOL_ID);
        data.put(MESSAGE_ID);
        data.putLong(c.sessionId);
        data.putLong(c.id);
        data.putShort(c.message.length.asInstanceOf[Short]);
        for (i <- 0 until c.message.length()) {
          data.putChar(c.message.charAt(i))
        }
        data.putShort(c.author.length.asInstanceOf[Short]);
        for (i <- 0 until c.author.length()) {
          data.putChar(c.author.charAt(i))
        }
        return data;
      }
      case a: NetworkMessageAck => {
        val data = ByteBuffer.allocate(1 + 2 + 8 + 8);
        data.putShort(PROTOCOL_ID);
        data.put(ACK_ID);
        data.putLong(a.sessionId);
        data.putLong(a.ackMsgId);
        return data
      }
    }
    throw new NetworkException("Unknown command " + message);
  }

  def decode(message: ByteBuffer): NetworkMessage = {
    val messageProtocol = message.getShort;
    if (messageProtocol != PROTOCOL_ID) {
      throw new NetworkException("Network protocol mismatch. Expected " + PROTOCOL_ID + " but was " + messageProtocol);
    }
    val parsedMessage = parseMessage(message)
    if (message.remaining > 0) {
      throw new NetworkException("Network message contains more bytes than expected");
    }
    return parsedMessage;
  }

  private def parseMessage(message: ByteBuffer): NetworkMessage = {
    val messageType = message.get
    messageType match {
      case CONNECT_ID => {
        val id = message.getLong
        val nrName = message.getShort
        val nameBuilder = new StringBuilder
        for (i <- 0 until nrName) {
          nameBuilder.append(message.getChar())
        }
        return new Connect(nameBuilder.toString.trim, id)
      }
      case DISCONNECT_ID => {
        val sessionId = message.getLong
        val reconnect = message.get() == 1;
        val msgId = message.getLong
        return new Disconnect(sessionId, reconnect, msgId)
      }
      case GAME_UPDATE_ID => {
        val sessionId = message.getLong
        val mapId = message.getLong
        val viewMode = message.get

        val baseX = message.getShort
        val baseY = message.getShort

        val nrObjects = message.getShort
        val objectList = new ListBuffer[(MapPosition, TileGraphics)]();
        for (i <- 0 until nrObjects) {
          val xDiff = message.get
          val yDiff = message.get
          val char = message.get.asInstanceOf[Char]
          val red = message.get
          val green = message.get
          val blue = message.get

          objectList += ((new MapPosition(xDiff + baseX, yDiff + baseY), new TileGraphics(char, red, green, blue)))
        }

        val nrLights = message.getShort
        val lightList = new ListBuffer[(MapPosition, TileLightSource)]();
        for (i <- 0 until nrLights) {
          val xDiff = message.get
          val yDiff = message.get
          val strength = message.get
          val red = message.get
          val green = message.get
          val blue = message.get
          lightList += ((new MapPosition(xDiff + baseX, yDiff + baseY), new TileLightSource(strength, red, green, blue)))

        }
        return new GameUpdate(sessionId, mapId, if (viewMode == 1) Ghost else Normal, new MapPosition(baseX, baseY), objectList.toList, lightList.toList)
      }
      case ACTION_COMMAND_ID => {
        val sessionId = message.getLong
        val actionId = message.getLong
        val x = message.getShort
        val y = message.getShort
        val msgId = message.getLong
        return new ActionCommand(sessionId, actionId, new MapPosition(x, y), msgId)
      }
      case MESSAGE_ID => {
        val sessionId = message.getLong
        val id = message.getLong
        val messageBuilder = new StringBuilder();
        val senderBuilder = new StringBuilder();

        val nrChat = message.getShort
        for (i <- 0 until nrChat) {
          messageBuilder.append(message.getChar())
        }

        val nrSender = message.getShort
        for (i <- 0 until nrSender) {
          senderBuilder.append(message.getChar())
        }
        return new ChatMessage(sessionId, id, messageBuilder.toString.trim, senderBuilder.toString.trim);
      }
      case ACK_ID => {
        val sessionId = message.getLong
        val ackId = message.getLong
        return new NetworkMessageAck(ackId, sessionId)
      }
    }
    throw new NetworkException("Unable to interpret network message");
  }

  private def readPositionUpdate(baseX: Short, baseY: Short, message: ByteBuffer): (MapPosition, Int) = {
    val xDiff = message.get
    val yDiff = message.get
    val graphics = message.getInt
    return (new MapPosition(xDiff + baseX, yDiff + baseY), graphics)
  }

}

object SimpleNetworkProtocol {
  val PROTOCOL_ID: Short = 1;
  val CONNECT_ID: Byte = 1;
  val SESSION_START_ID: Byte = 2;
  val DISCONNECT_ID: Byte = 3;
  val GAME_UPDATE_ID: Byte = 4;
  val ACTION_COMMAND_ID: Byte = 5;
  val MESSAGE_ID: Byte = 6;
  val ACK_ID: Byte = 7;

}