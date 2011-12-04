package se.faerie.sleep.common.network
import NetworkProtocol.NetworkData
import scala.collection.mutable.Map;
import scala.collection.mutable.Set;

class LimitedMessageReliability {

    private class MessageInfo(val data: NetworkData, val time: Long)
    private val sendMessages = Map[Long, MessageInfo]()
    private val recievedMessages = Map[Long,Long]()
    private var maxId : Long=Long.MinValue

    def clearOldMessages(maxAge : Long){
       sendMessages.retain((k,v) => v.time > maxAge)
       recievedMessages.retain((k,v) => v > maxAge)
    }
    
    def messageSend(id : Long,time : Long, data : NetworkData){
        sendMessages += (id -> new MessageInfo(data, time))
    }
    
    def getWaitingMessages(minAge : Long) : Traversable[NetworkData] = {
      sendMessages.filter(v => (v._2.time < minAge)).map(d => d._2.data);
    }
    
    def messageRecieved(id : Long,time : Long){
      recievedMessages += id -> time;
      if(id>maxId){
        maxId=id;
      }
    }
    
    def messageAcknoledged(id :Long){
      sendMessages -= id;
    }
    
    def isMessageDuplicate(id : Long) = {
      id<maxId && recievedMessages.isDefinedAt(id)
    }
    
    def isActive() = !sendMessages.isEmpty || !recievedMessages.isEmpty
 
    
  
}