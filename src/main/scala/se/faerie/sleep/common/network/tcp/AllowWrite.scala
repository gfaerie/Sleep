package se.faerie.sleep.common.network.tcp

import java.nio.channels.SocketChannel

case class AllowWrite(channel : SocketChannel) extends ServerCommand{

}