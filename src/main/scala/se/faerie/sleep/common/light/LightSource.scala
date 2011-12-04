package se.faerie.sleep.common.light

trait LightSource {
	def castLight(lightCallback: (Int, Int, Double,Double,Double)=> Unit, blockFunction: (Int, Int) => Boolean, centerX : Int, centerY : Int, frameTime : Long);
	def touchesArea(centerX : Int, centerY : Int, startX : Int, startY : Int, endX : Int, endY : Int, frameTime : Long) : Boolean
}