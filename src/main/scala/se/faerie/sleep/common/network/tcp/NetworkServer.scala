package se.faerie.sleep.common.network.tcp

import java.nio.channels.spi.SelectorProvider;
import scala.actors.Actor;
import java.nio.channels._;import java.net.InetSocketAddress;import scala.collection.mutable.Queue;
class NetworkServer(port : Int,controller : Actor) extends Runnable with NetworkServerAccess {

	private var go = true;
	private val serverChannel = ServerSocketChannel.open;	private val commandQueue = new Queue[ServerCommand];	
	private val selector ={
			val selector = SelectorProvider.provider.openSelector;
			val address = new InetSocketAddress(port);			serverChannel.configureBlocking(false);			serverChannel.socket.bind(address);			serverChannel.register(selector, SelectionKey.OP_ACCEPT); 			(selector) 
	};		def addCommand(command : ServerCommand){		commandQueue+=command;		selector.wakeup;	};		def stop={		go=false;		selector.wakeup;	};

	def run(): Unit = {  
			while(go){				
				handleCommands();				
				selector.select;				val selectedKeys = selector.selectedKeys.iterator;				while(selectedKeys.hasNext){					val selectedKey = selectedKeys.next.asInstanceOf[SelectionKey];					selectedKeys.remove();					if(!selectedKey.isValid){						handleDisconnect(selectedKey);					}					else if(selectedKey.isAcceptable){						handleAccept(selectedKey);					}					else{ 						if(selectedKey.isReadable){							handleRead(selectedKey);						};						if(selectedKey.isWritable){							handleWrite(selectedKey);						};					}				} 
			};			selector.close();			serverChannel.close();
	};

	def handleCommands(){
		while(!commandQueue.isEmpty){			val command = commandQueue.dequeue;			command match{				case AllowRead(channel) =>{					val key = channel.keyFor(selector);					key.interestOps(key.interestOps|SelectionKey.OP_READ);				}				case AllowWrite(channel) =>{					val key = channel.keyFor(selector);					key.interestOps(key.interestOps|SelectionKey.OP_WRITE);				}				case Disconnect(channel) =>{					val key = channel.keyFor(selector);					handleDisconnect(key);				}			};		}
	};

	def handleRead(key : SelectionKey){
		val channel = key.channel.asInstanceOf[SocketChannel];		key.interestOps(key.interestOps & ~SelectionKey.OP_READ);		controller ! new ReadDelegate(channel);
	};

	def handleWrite(key : SelectionKey){
		val channel = key.channel.asInstanceOf[SocketChannel];		key.interestOps(key.interestOps & ~SelectionKey.OP_WRITE);		controller ! new WriteDelegate(channel);
	};

	def handleAccept(key : SelectionKey){
		val channel = key.channel.asInstanceOf[ServerSocketChannel];		val socketChannel = channel.accept();		socketChannel.configureBlocking(false);		socketChannel.register(selector, SelectionKey.OP_READ);
	};	def handleDisconnect(key : SelectionKey){		key.channel.close();		key.cancel();	};
}