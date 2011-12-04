package se.faerie.sleep.common.light

trait LineOfSightCalculator {

	def calculateLos(losCallback: (Int, Int)=> Unit, 
			blockFunction: (Int, Int) => Boolean, 
			centerX: Int, 
			centerY: Int)
	
	def calculateLos(losCallback: (Int, Int)=> Unit, 
			blockFunction: (Int, Int) => Boolean, 
			centerX: Int, 
			centerY: Int,
			startAngle:Double,
			endAngle:Double)
	
	def getMaxCastLength : Double;
	
}