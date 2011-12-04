package se.faerie.sleep.common.light

import collection.mutable.ListBuffer;
import collection.mutable.Set;
import se.faerie.sleep.common._;
import scala.math._
import Ordering.Implicits._

class LineOfSightCalculatorImpl(length: Double) extends LineOfSightCalculator with GraphicsHelper {

	// transformation from octant one to the other octants
	private val  xTransform = List(1,1,1,1,-1,-1,-1,-1)
	private val  yTransform = List(1,1,-1,-1,-1,-1,1,1)
	private val invertTransform = List(false,true,true,false,false,true,true,false)	
	private val invertBeamOrderOnBlock = List(false,true,false,true,false,true,false,true,false)
	
	// size of an octant
	private val angleSpread: Double =Pi/4;
	
	val (allPositionsList,positionRayMap,numberOfRays) ={
		
		// build lightrays
		val allOuterRays: List[List[MapPosition]] = buildLines(0,0,0,angleSpread,length);
		
		// mapping between positions and lightrays
		val positionRayMap = Array.ofDim[Int](ceil(length).toInt+1, ceil(length).toInt+1, allOuterRays.length);
		
		// all positions
		val allPositionsSet= Set[MapPosition]()
		allOuterRays.foreach(l => allPositionsSet++=l)
		val allPositionsList= allPositionsSet.toList.sortWith(_ < _)
		
		// process each position check which beams lit which positions
		allPositionsList.foreach(p =>{
			val affiliatedRays = new ListBuffer[Int]();
			for(i <- 0 until allOuterRays.length){
				val ray =allOuterRays(i);
				if(ray.contains(p)){
					affiliatedRays+=i
				}
			}
			positionRayMap(p.x)(p.y)=affiliatedRays.toArray
		})
	
	// initialize variables
	(allPositionsList,positionRayMap,allOuterRays.length)
	}

	def getMaxCastLength = length;
	
	def calculateLos(losCallback: (Int, Int)=> Unit, 
			blockFunction: (Int, Int) => Boolean, 
			centerX: Int, 
			centerY: Int){
		val emptyBlockSet = Set[Int]()
		for(octant <- 0 to 7){
			processOctant(losCallback,blockFunction,centerX,centerY,xTransform(octant),yTransform(octant),invertTransform(octant), emptyBlockSet)
		}
	}

	def calculateLos(losCallback: (Int, Int)=> Unit, 
			blockFunction: (Int, Int) => Boolean, 
			centerX: Int, 
			centerY: Int,
			startAngle:Double,
			endAngle:Double){

		// angle interval
		val interval : Double = endAngle - startAngle;

		// full circle
	if(interval >2*Pi){
		calculateLos(losCallback,blockFunction,centerX,centerY);
	}
	
	// part of the circle
	else{

		// check which octant we should start in
		var normStartAngle = normalizeAngle(startAngle);
		val startPoint = normStartAngle/angleSpread
		val startOctant = floor(startPoint).toInt;
		val nrStartBlockedRays = round((startPoint-startOctant)*numberOfRays).toInt
		val startBlockedRays = for { i <- 0 until nrStartBlockedRays } yield (if(!invertBeamOrderOnBlock(startOctant)) i else numberOfRays-1-i);

		// transform startangle to 0  to 2 Pi interval
		var normEndAngle = normalizeAngle(endAngle);
		val endPoint = normEndAngle/angleSpread
		val endOctant = floor(endPoint).toInt;
		val nrEndBlockedRays = round((endOctant+1-endPoint)*numberOfRays).toInt
		val endBlockedRays = for { i <- 0 until nrEndBlockedRays } yield (if(invertBeamOrderOnBlock(endOctant)) i else numberOfRays-1-i);

		// check how many octants we need to traverse
		val octants = if(normStartAngle>=normEndAngle) (endOctant-startOctant +8) else (endOctant-startOctant);

		// start processing each octant
		for(i <- 0 to octants){
			var octantNumber=(i+startOctant)%8;

			val blockedRays = Set[Int]()

			if(i==0){
				blockedRays++=startBlockedRays
			}

			if(i==octants){
				blockedRays++=endBlockedRays
			}
			processOctant(losCallback,blockFunction,centerX,centerY,xTransform(octantNumber),yTransform(octantNumber),invertTransform(octantNumber), blockedRays)
		}

	}
	}

	private def processOctant(lightCallback: (Int, Int)=> Unit, 
			blockFunction: (Int, Int) => Boolean, 
			centerX: Int, 
			centerY: Int, xTransform: Int, yTransform : Int, invertTransform: Boolean, preBlockedRays: Set[Int]) {
		val blockedRays = new Array[Boolean](numberOfRays);
		var nrBlockedRays=0;

		if(preBlockedRays.size>=numberOfRays){
			return;
		}

		for(blocked <- preBlockedRays){
			blockedRays(blocked)=true;
			nrBlockedRays+=1;
		}


		var positionIndex=0;
		for (p <- allPositionsList) {
			val x = xTransform * (if(invertTransform) p.y else p.x) + centerX;
			val y = yTransform * (if(invertTransform) p.x else p.y) + centerY;
			val affliatedRays = positionRayMap(p.x)(p.y);
			var lit = false;
			var rayNumber=0;
			while (rayNumber<affliatedRays.size && !lit) {
				val ray = affliatedRays(rayNumber)
				if(!blockedRays(ray)) {
					lightCallback(x, y);
					lit=true;
				}
				rayNumber+=1;
			}
			if(blockFunction(x, y)) {
				for (blockedRay <- affliatedRays) {
					if(!blockedRays(blockedRay)){
						blockedRays(blockedRay)=true
						nrBlockedRays+=1;
						if(nrBlockedRays>=numberOfRays){
							return;
						}
					}
				}
			}
		}
	}

	private def normalizeAngle(angle: Double) : Double = {
			var workAngle = (if(angle>2*Pi||angle<0) angle % (2*Pi) else angle);
			if (workAngle < 0) workAngle+=2*Pi;
			return workAngle;
	}
}
