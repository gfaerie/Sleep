package se.faerie.sleep.server.ai
import se.faerie.sleep.server.state.GameObject

trait AggressionHandler {
  val aggroLimit : Double;
  val maxRange : Double;
  val currentTargetBonus : Double;
  val groupTargetBonus : Double;
  val latestAttackerBonus : Double;
  def objectBonus(o : GameObject) : Double;
  def freeTileBonus(nr : Int): Double;
  def blockedTileBonus(nr : Int): Double;
}