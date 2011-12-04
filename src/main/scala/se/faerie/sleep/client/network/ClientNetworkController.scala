package se.faerie.sleep.client.network

import java.net.SocketAddress
import se.faerie.sleep.client.PlayerCommands._
import se.faerie.sleep.client.view.graphics.GraphicsState
import se.faerie.sleep.client.view.View
import akka.actor.Actor._
import akka.actor.ActorRef
import akka.actor.Actor
import java.net.InetSocketAddress
import se.faerie.sleep.common.network.NetworkProtocol._
import se.faerie.sleep.common.MapPosition
import se.faerie.sleep.common.network.LimitedMessageReliability
import se.faerie.sleep.common.network.NetworkProtocol
import se.faerie.sleep.client.view.graphics.MinimapState
import scala.swing.Swing

abstract class ClientNetworkController(sender: ActorRef, renderer: ActorRef,
                                       view: View) extends Actor with NetworkProtocol {

  private val serverPort = 11400;

  // TODO not very nice...think of something better
  view.playerActionHandler = this.self;

  val messageReliability = new LimitedMessageReliability();
  var serverAddress: SocketAddress = null;

  private var sessionId: java.lang.Long = null;

  def receive = {
    case shutdown: PlayerShutdown => {
      // disconnect old session if such exists block until disconnect is sent
      if (serverAddress != null && sessionId != null) {
        view.showLog("Disconnecting from server");
        sender !! (new ConfirmableNetworkData(new NetworkData(encode(new Disconnect(sessionId)), serverAddress)), shutdown.timeout)
      }
      registry.shutdownAll()
      System.exit(0);
    }
    case action: PlayerAction => {
      sendMessage(new ActionCommand(sessionId, action.id, new MapPosition(action.gameX, action.gameY)))
    }
    case chat: PlayerChat => {
      sendMessage(new ChatMessage(sessionId, chat.message))
    }
    case connect: PlayerConnect => {
      // disconnect old session if such exists
      if (serverAddress != null && sessionId != null) {
        sendMessage(new Disconnect(sessionId));
      }
      // connect to new server
      serverAddress = new InetSocketAddress(connect.serverAddress, serverPort);
      view.showLog("Connecting to " + connect.serverAddress + " as " + connect.name);
      sendMessage(new Connect(connect.name))
    }
    case g: GraphicsState => {
      Swing.onEDT({ view.showGraphics(g) })
    }
    case m: MinimapState => {
      Swing.onEDT({ view.showMinimap(m) })
    }
    // clear old data, we tried to resend but server won't ack, stop resending
    case c: ClearData => {
      messageReliability.clearOldMessages(c.age)
    }
    // resend old data that has not been acked
    case r: ResendData => {
      messageReliability.getWaitingMessages(r.age).foreach(d => sender ! d.duplicate)
    }
    // server has message for client
    case n: NetworkData => {

      val message = decode(n.data);

      message match {
        // client ack of server message
        case a: NetworkMessageAck => {
          messageReliability.messageAcknoledged(a.ackMsgId);
        }
        case d: Disconnect => {

          // TODO: handle disconnect
          if (d.sessionId == sessionId) {
            this.sessionId = null;
          }
          ackMessage(d.id, d.sessionId);
        }
        case s: SessionMessage => {
          // ignore messages if we are not connected
          if (serverAddress != null) {
            if (s.sessionId != sessionId) {
              view.showLog("New session started with " + n.address);
            }
            // wrong server send disconnect
            if (!(n.address equals serverAddress)) {
              val disconnect = new Disconnect(s.sessionId)
              val data = new NetworkData(encode(disconnect), serverAddress)
              messageReliability.messageSend(disconnect.id, System.currentTimeMillis, data.duplicate)
              sender ! data
            } // correct server
            else {
              sessionId = s.sessionId;
              s match {
                // render game and send it to drawer
                case g: GameUpdate => {
                  renderer ! g;
                }
                case message: ChatMessage => {
                  if (!messageReliability.isMessageDuplicate(s.id)) {
                    if (message.author == null || message.author.isEmpty) {
                      view.showLog(message.message)
                    } else {
                      view.showChat(message.author, message.message)
                    }
                    ackMessage(s.id, s.sessionId)
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  def sendMessage(n: NetworkMessage) {
    if (serverAddress != null) {
      val data = new NetworkData(encode(n), serverAddress)
      messageReliability.messageSend(n.id, System.currentTimeMillis, data.duplicate)
      sender ! data
    }
  }

  def ackMessage(msgId: Long, clientId: Long) {
    sender ! new NetworkData(encode(new NetworkMessageAck(msgId, clientId)), serverAddress)
  }

}