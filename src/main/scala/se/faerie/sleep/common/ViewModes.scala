package se.faerie.sleep.common

object ViewModes {
  trait ViewMode
  case object Normal extends ViewMode
  case object Ghost extends ViewMode
}