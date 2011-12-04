package se.faerie.sleep.common.network.tcp;import java.util.concurrent.atomic.AtomicInteger
import scala.actors.Actor
import java.nio.channels.SocketChannel
import java.nio.ByteBuffer
import scala.collection.mutable.{Queue,Map};

private class Client(val channel : SocketChannel, val id : Int){
	var readBuffer = ByteBuffer.allocate(Short.MaxValue);
	val readSizeBuffer = ByteBuffer.allocate(2);
	val writeQueue = new Queue[ByteBuffer];
	var writing = false;
}
private case class Read(client: Client)
private case class Write(client: Client)
private case class ReadDone(client: Client, id : Int)
private case class WriteDone(client: Client, id : Int)

private class NetworkWorker(controller: Actor, val id : Int) extends Actor{
	
	def act(): Unit = {  
		loop
		{
			react
			{
				case Read(client) => {
					if(client.readSizeBuffer.hasRemaining){
						client.channel.read(client.readSizeBuffer);
					}
					if(!client.readSizeBuffer.hasRemaining){
						client.readBuffer.limit(client.readSizeBuffer.getShort(0))
						client.channel.read(client.readBuffer);
					}
					controller ! ReadDone(client, id)
				}
				case Write(client) => {
					if(!client.writeQueue.isEmpty){
						client.channel.write(client.writeQueue.front)
					}
					controller ! WriteDone(client, id)
				}
			}
		}
	}
} 

class NetworkController(serverAccess : NetworkServerAccess, nrWorkers : Int) extends Actor {

	private val workLoad = new Array[Int](nrWorkers);	
	private val workers = new Array[NetworkWorker](nrWorkers);	
	private val clientsIndexedByChannel = Map[SocketChannel,Client]();
	private val clientsIndexedById = Map[Int,Client]();
	private val idCounter = new AtomicInteger();
	
	
	def act(): Unit = {	
	  loop
		{
			react
			{
				case ReadDelegate(channel) =>{
					val workerId = getWorker
					workLoad(workerId)+=1;
					workers(workerId) ! Read(getClient(channel))
				}
				case WriteDelegate(channel) =>{
					val workerId = getWorker
					workLoad(workerId)+=1;
					val client = getClient(channel)
					client.writing=true;
					workers(workerId) ! Write(getClient(channel))
				}
				case ReadDone(client,id) =>{
					workLoad(id)-=1;
					if(!client.readBuffer.hasRemaining){
						client.readBuffer.flip();
						client.readSizeBuffer.flip();
						val data = new Array[Byte](client.readBuffer.capacity);
						client.readBuffer.get(data)
						
						//TODO: Do something with the read bytes
					}
					serverAccess.addCommand(new AllowRead(client.channel))
				}
				case WriteDone(client,id) =>{
					workLoad(id)-=1;
					client.writing=false;
					if(!client.writeQueue.front.hasRemaining){
						client.writeQueue.dequeue
					}
					if(!client.writeQueue.isEmpty){
						serverAccess.addCommand(new AllowWrite(client.channel))
					}
				}
				case Disconnect(channel) => {
				    serverAccess.addCommand(new Disconnect(channel))
				}
				case ClientUpdate(clientId, data) => {
					val client = clientsIndexedById(clientId);
					val dataLength = data.capacity;
					val lengthBuffer = ByteBuffer.allocateDirect(2);
					lengthBuffer.putShort(dataLength.asInstanceOf[Short]);
					client.writeQueue.enqueue(lengthBuffer);
					client.writeQueue.enqueue(data);
					if(!client.writing){
						serverAccess.addCommand(new AllowWrite(client.channel))
					}
				}
		
			}
		}
  }	
	
  override def start : Actor ={
	  workers.foreach(f => f.start)
	  return super.start
  }	
  
    
  private def getClient(channel : SocketChannel) : Client ={
	  if(clientsIndexedByChannel.contains(channel)){
	 	  return clientsIndexedByChannel(channel);
	  }
	  else{
		  val client = new Client(channel,idCounter.incrementAndGet);
	 	  clientsIndexedByChannel+(channel -> client);
	 	  clientsIndexedById+(client.id -> client);
	 	  return client;
	  }
  }
  
  private def getWorker() : Int ={
	  var minLoad = Int.MaxValue;
	  var minId : Int = 0;
	  for(i <- 0 to nrWorkers){
		if(workLoad(i)==0){
			return i;
		}
		else if(workLoad(i)<=minLoad){
			minId=i;
			minLoad=workLoad(i);
		}				
	  }
	  return minId; 
  }
 
}