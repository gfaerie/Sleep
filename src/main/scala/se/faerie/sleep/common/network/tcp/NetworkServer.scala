package se.faerie.sleep.common.network.tcp

import java.nio.channels.spi.SelectorProvider;

import java.nio.channels._;
class NetworkServer(port : Int,controller : Actor) extends Runnable with NetworkServerAccess {

	private var go = true;

	private val selector ={
			val selector = SelectorProvider.provider.openSelector;
			val address = new InetSocketAddress(port);
	};

	def run(): Unit = {  
			while(go){
				handleCommands();
				selector.select;
			};
	};

	def handleCommands(){
		while(!commandQueue.isEmpty){
	};

	def handleRead(key : SelectionKey){
		val channel = key.channel.asInstanceOf[SocketChannel];
	};

	def handleWrite(key : SelectionKey){
		val channel = key.channel.asInstanceOf[SocketChannel];
	};

	def handleAccept(key : SelectionKey){
		val channel = key.channel.asInstanceOf[ServerSocketChannel];
	};
}