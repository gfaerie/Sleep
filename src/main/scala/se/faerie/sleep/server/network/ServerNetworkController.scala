package se.faerie.sleep.server.network
import collection.mutable.Map
import java.net.SocketAddress
import scala.util.Random
import akka.actor._
import se.faerie.sleep.server.state.update.GameStateUpdateContext
import se.faerie.sleep.common.network.NetworkProtocol._
import se.faerie.sleep.server.state.update.helper.SingleUpdate
import se.faerie.sleep.server.state.GameObject
import se.faerie.sleep.common.MapPosition
import se.faerie.sleep.common.network.NetworkProtocol
import se.faerie.sleep.common.network.LimitedMessageReliability
import se.faerie.sleep.server.player._
import se.faerie.sleep.server.ServerCommands._
import se.faerie.sleep.server.state.GameStateFactory

abstract class ServerNetworkController(game: ActorRef, sender: ActorRef, playerFactory: PlayerFactory, playerActionFactory: PlayerActionFactory, gameStateFactory: GameStateFactory) extends Actor with NetworkProtocol {

  private class ClientInfo(val address: SocketAddress, val name: String)

  private class RemoveClient(objectId: Long) extends SingleUpdate {
    def doUpdate(context: GameStateUpdateContext) {
      try {
        context.state.removeObject(objectId);
      } catch {
        //object already remove no need to worry
        case e: NoSuchElementException => {}
      }
    };
    priority=1000;
    override def toString = "Player removal for client " + objectId;
  };

  private class AddClient(player: GameObject) extends SingleUpdate {
    def doUpdate(context: GameStateUpdateContext) {
      val random = new Random()
      // add at random position
      while (true) {
        val x = random.nextInt(context.state.width);
        val y = random.nextInt(context.state.height);
        if (context.state.getBackground(x, y).passable) {
          context.state.addObject(new MapPosition(x, y), player);
          return ;
        }
      }
    };
    override def toString = "Player add for client " + player.id;
    priority=1000;
  };

  private val clients = Map[Long, ClientInfo]();
  private val messageReliability = Map[Long, LimitedMessageReliability]();

  def receive = {
    // current game is over, disconnect all clients but tell them its cool to reconnect
    case reset: ResetGame => {
      clients.foreach(c => {
        sender ! new NetworkData(encode(new Disconnect(c._1, true)), c._2.address)
      })
      clients.clear
      game ! gameStateFactory.createNewGameState(this.self)
    }
    // shutdown server, make sure we disconnect all clients first
    case shutdown: Shutdown => {
      sender !! new ConfirmableNetworkData(clients.map(c => (new NetworkData(encode(new ChatMessage(c._1, "Server shutting down")), c._2.address))).toList)
      sender !! new ConfirmableNetworkData(clients.map(c => (new NetworkData(encode(new Disconnect(c._1, false)), c._2.address))).toList)
      clients.clear
      self.stop
      System.exit(0)
    }
    // clear old data, we tried to resend but client refuses ack, their loss, stop resending
    case clear: ClearData => {
      messageReliability.foreach(v => (v._2.clearOldMessages(clear.age)))
      
      // throw out old storage
      //messageReliability.retain((k,v) => (clients.contains(k)||v.isActive()))
    }
    // resend old data that has not been acked
    case resend: ResendData => {
      messageReliability.foreach(v => (v._2.getWaitingMessages(resend.age).foreach(d => sender ! d.duplicate)))
    }
    // server requested disconnect client
    case dis: Disconnect => {
      disconnectClient(dis.sessionId);
    }
    // server want to send game update, if it doesn't reach the client its already out of date so no resend
    case update: GameUpdate => {
      val address = clients(update.sessionId).address
      val data = encode(update)
      sender ! new NetworkData(data, address)
    }
    // server want to send something to a client we need to make sure reaches it
    case other: SessionMessage => {
      val address = clients(other.sessionId).address
      val data = new NetworkData(encode(other), address)
      messageReliability(other.sessionId).messageSend(other.id, System.currentTimeMillis, data.duplicate)
      sender ! data
    }
    // client has sent something to server
    case n: NetworkData => {
      try {
        val message = decode(n.data)
        message match {
          // new clients wants to connect, always process connects even if we have seen them before	
          case c: Connect => {
            val client = new ClientInfo(n.address, c.name);
            clients.find(a => ((a._2.name equals c.name) && (a._2.address equals n.address))) match {
              // client already exists, resume
              case Some(oldclient) => {
                ackMessage(c.msgId, oldclient._1, n.address)
              }
              // new client
              case None => {
                val player = playerFactory.createPlayer(-1, c.name)
                messageReliability += (player.id -> new LimitedMessageReliability())
                clients += (player.id -> new ClientInfo(n.address, c.name))
                addPlayerToGame(player)
                ackMessage(c.msgId, player.id, n.address)
              }
            }
          }
          // client ack of server message
          case a: NetworkMessageAck => {
            messageReliability(a.sessionId).messageAcknoledged(a.ackMsgId)
          }
          // connected client has sent command
          case s: SessionMessage => {
            clients.get(s.sessionId) match {
              case None => {
                sender ! new NetworkData(encode(new Disconnect(s.id)), n.address)
              }
              case Some(c) => {
                if (!c.address.equals(n.address)) {
                  sender ! new NetworkData(encode(new Disconnect(s.id)), n.address)
                } else if (!messageReliability(s.sessionId).isMessageDuplicate(s.id)) {
                  // handle main message
                  s match {
                    // client want to disconnect	
                    case dis: Disconnect => {
                      disconnectClient(dis.sessionId)
                    }
                    // client want to execute an action
                    case a: ActionCommand => {
                      val action = playerActionFactory.createAction(a.sessionId, a.actionId, a.targetPosition)
                      if (action != null) {
                        game ! action;
                      }
                    }
                    case c: ChatMessage => {
                      val author = clients(c.sessionId).name
                      clients.foreach(client => self ! new ChatMessage(client._1, c.message, author))
                    }
                  }
                }
              }
            }

            // always ack even if msg is duplicate, client didn't get ack
            ackMessage(s.id, s.sessionId, n.address);
          }
        }

      } catch {
        case e: Exception => { e.printStackTrace }
      }
    }

  }

  private def disconnectClient(objectId: Long) {
    messageReliability -= objectId
    clients -= objectId
    game ! new RemoveClient(objectId)
  }

  private def addPlayerToGame(player: GameObject) {
    game ! new AddClient(player)
  }

  private def ackMessage(msgId: Long, clientId: Long, address: SocketAddress) {
    sender ! new NetworkData(encode(new NetworkMessageAck(msgId, clientId)), address)
  }

}