package se.faerie.sleep.client.view.graphics.light

trait LightSource {
	def castLight(lightCallback: (Int, Int, Double,Double,Double)=> Unit, blockFunction: (Int, Int) => Boolean, frameTime : Long);
}