package se.faerie.sleep.common.network.tcp
import java.nio.channels.SocketChannel

case class Disconnect(channel : SocketChannel) extends ServerCommand{

}