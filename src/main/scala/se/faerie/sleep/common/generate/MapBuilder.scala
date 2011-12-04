package se.faerie.sleep.common.generate
import se.faerie.sleep.common.GameBackground._;

trait MapBuilder {
	def buildBackground(seed : Long) : Array[Array[GameBackground]];
}