package se.faerie.sleep.common.light
import scala.util.Random;
import java.awt.Color;
import scala.math._

class StaticSphericalLightSource(red: Double, green: Double, blue : Double,maxRadius:Int, losCalculator : LineOfSightCalculator) extends LightSource {
	
	val random = Random
	
	def castLight(lightCallback: (Int, Int, Double,Double,Double)=> Unit,blockFunction: (Int, Int) => Boolean, centerX : Int, centerY : Int, frameTime : Long){
		val processed = Array.ofDim[Boolean](2*maxRadius+1,2*maxRadius+1);
		def distance(x: Int, y: Int) : Double =  (x-centerX)*(x-centerX)+(y-centerY)*(y-centerY);
		
		val redBase=0.2*sin(frameTime/1551610.0);
		val greenBase=0.2*sin(frameTime/2751610.0);
		val blueBase=0.2*sin(frameTime/651610.0);	
		val allBase=0.1*sin(frameTime/3051610.0);
		// los does not guarantee that each position is only called once
		def losCallback = (x: Int, y: Int) => {
		
			val processedXPos = centerX-x+maxRadius;
			val processedYPos = centerY-y+maxRadius;
			if(!processed(processedXPos)(processedYPos)){
				val distanceAttrition = 1/(distance(x,y)+0.1);
				lightCallback(x,y,(1+redBase+allBase)*red*distanceAttrition,(1+greenBase+allBase)*green*distanceAttrition,(1+blueBase+allBase)*blue*distanceAttrition);
				processed(processedXPos)(processedYPos)=true;
			}
		};
		losCalculator.calculateLos(losCallback,blockFunction,centerX,centerY);
	}
	
	def touchesArea(centerX : Int, centerY : Int, startX : Int, startY : Int, endX : Int, endY : Int, frameTime : Long) : Boolean = 
		!(centerY+maxRadius<startY||centerY-maxRadius>endY||centerX+maxRadius<startX||centerX-maxRadius>endX);
	
}