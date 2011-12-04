package se.faerie.sleep.common

import collection.mutable.ListBuffer;
import scala.math._
trait GraphicsHelper {

  def buildLines(startX: Int, startY: Int, startAngle: Double, endAngle: Double, length: Double): List[List[MapPosition]] = {
    var listBuffer = new ListBuffer[List[MapPosition]]
	val angleDiff = (endAngle-startAngle) / (ceil((endAngle-startAngle)/(asin(1 / length))));
    var workAngle = startAngle
    while (workAngle <= (endAngle + 0.01)) {
      listBuffer += oldBuildLine(startX, startY, workAngle, length,0.5)
      workAngle += angleDiff;
    }
    return listBuffer.toList
  }

  def buildLine(startX: Int, startY: Int, angle: Double, length: Double): List[MapPosition] = {
    val xRatio = cos(angle)*length
    val yRatio = sin(angle)*length
    return buildLine(startX, startY, floor(startX+xRatio).toInt, floor(startY+yRatio).toInt)
  }
  
  def buildLine(startX: Int, startY: Int, endX: Int, endY : Int): List[MapPosition] = {
    var listBuffer = new ListBuffer[MapPosition]
    val dx = abs(endX-startX)
    val dy = abs(endY-startY)
    val sx = if(startX < endX) 1 else -1;
    val sy = if(startY < endY) 1 else -1;
    var diff= dx-dy;
    var x = startX;
    var y = startY;
    while(!(x==endX && y ==endY)){
    	listBuffer += new MapPosition(x, y)
    	val loopDiff= 2*diff; 
    	if(loopDiff> -dy){
    		  diff = diff - dy;
    		  x = x + sx;
    	}
    	if(loopDiff < dx){
    		diff = diff +dx;
    		y = y +sy;
    	}
    }
    listBuffer += new MapPosition(x, y)
    return listBuffer.toList
  }
  
  	private def oldBuildLine(startX: Int, startY: Int, angle: Double, length: Double, stepSize : Double) : List[MapPosition] ={
			var listBuffer = new ListBuffer[MapPosition]
			val xRatio = cos(angle)
			val yRatio = sin(angle)
			var workLength=length
			
			while (workLength >= 0) {
				var endX = startX + xRatio * workLength
				var endY = startY + yRatio * workLength
				val x= round(endX.toFloat).asInstanceOf[Short];
				val y=round(endY.toFloat).asInstanceOf[Short];
				if(listBuffer.size!=0){
					val last = listBuffer.last;

					if(x!=last.x || y!=last.y){
						listBuffer += new MapPosition(x,y)
					}
				}
				else{
					listBuffer += new MapPosition(x,y)
				}
				workLength -= stepSize
			}
			return listBuffer.toList
	}

}
