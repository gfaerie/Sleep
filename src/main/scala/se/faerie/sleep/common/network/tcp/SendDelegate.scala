package se.faerie.sleep.common.network.tcp

import java.nio.channels.DatagramChannel
import java.nio.ByteBuffer
import java.net.SocketAddress

case class SendDelegate(socket : DatagramChannel, data: ByteBuffer, target : SocketAddress) {

}