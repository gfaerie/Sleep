package se.faerie.sleep.common.network.tcp

import java.nio.channels.SocketChannel
import java.nio.ByteBuffer
import scala.collection.mutable.Queue

class RemoteClient(val id : Int, val socketChannel : SocketChannel) {
	val readQueue = new Queue[ByteBuffer];
	val writeQueue = new Queue[ByteBuffer];
}