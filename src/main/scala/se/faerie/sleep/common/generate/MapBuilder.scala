package se.faerie.sleep.common.generate


trait MapBuilder {
	def buildBackground(seed : Long) : Array[Array[GameBackground]];
}