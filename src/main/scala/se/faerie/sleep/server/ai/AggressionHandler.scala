package se.faerie.sleep.server.ai
import se.faerie.sleep.server.state.GameObject

trait AggressionHandler {
  val patrolLimit : Double;
  val aggroLimit : Double;
  val maxPursuitTime : Long;
  val maxRange : Double;
  val currentTargetBonus : Double;
  val groupTargetBonus : Double;
  val latestAttackerBonus : Double;
  def objectBonus(o : GameObject) : Double;
  def tileBonus(nrFree : Int, nrBlocked : Int): Double;
}