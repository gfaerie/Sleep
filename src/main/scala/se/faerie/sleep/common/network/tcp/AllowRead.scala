package se.faerie.sleep.common.network.tcp
import java.nio.channels.SocketChannel

case class AllowRead(channel : SocketChannel) extends ServerCommand{

}