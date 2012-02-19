package se.faerie.sleep.server.state.update
import scala.collection.mutable.Set
import se.faerie.sleep.server.ServerCommands._
import akka.actor.Actor
import se.faerie.sleep.server.state.GameState
import scala.collection.mutable.ListBuffer

class GameStateUpdateActor extends Actor with GameStateUpdateContext {

  private var updatersToRemove = Set[GameStateUpdater]()
  private var updatersToAdd = Set[GameStateUpdater]()
  private var updaters = List[GameStateUpdater]()
  private var logs = ListBuffer[String]()
  private var gameState: GameState = null
  var updateTime: Long = 0;
  var lastUpdateTime : Long =0;
  var startTime : Long =0;
  
  def receive = {
    case s: GameStateData => {
      startTime=System.nanoTime
      gameState = s.state
      updaters = List.empty
      updatersToRemove.clear
      updatersToAdd.clear
      s.initialUpdaters.foreach(u => addUpdater(u))
      val time = System.nanoTime
      lastUpdateTime = time;
      updateTime = time;
    }
    case u: GameStateUpdate => {
      logs.clear
      lastUpdateTime = updateTime;
      updateTime = System.nanoTime;
      setupUpdaters
      update
    }
    case g: GameStateUpdater => {
      addUpdater(g)
    }

  }
  def state = gameState

  def getLogs: Traversable[String] = logs
  def addLog(log: String) = { logs += log }

  def addUpdater(updater: GameStateUpdater) {
    updatersToAdd += updater
  }
  def removeUpdater(updater: GameStateUpdater) {
    updatersToRemove += updater
  }

  private def setupUpdaters = {
    var handlerHolder = Set[GameStateUpdater]();
    handlerHolder ++= updaters
    handlerHolder --= updatersToRemove
    handlerHolder --= updatersToAdd
    handlerHolder ++= updatersToAdd
    updatersToRemove.clear
    updatersToAdd.clear
    updaters = handlerHolder.toList.sortWith(_.priority > _.priority)
  }

  private def update = {
    updaters.foreach(s =>
      {
        try {
          s.update(this)
        } catch {
          //TODO add logging
          case e: Exception => e.printStackTrace()
        }
      });
  }

}