package se.faerie.sleep.client
import java.net.SocketAddress

object PlayerCommands {

  trait PlayerCommand
  case class PlayerAction(val id : Long, val gameX : Int, val gameY : Int) extends PlayerCommand
  case class PlayerChat(val message : String) extends PlayerCommand
  case class PlayerConnect(val name : String, val serverAddress: String) extends PlayerCommand
  case class PlayerShutdown(val timeout : Long) extends PlayerCommand

}