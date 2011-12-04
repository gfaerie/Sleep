package se.faerie.sleep.common.generate
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Queue
import scala.math.max
import scala.math.min
import scala.math.sqrt
import scala.util.Random

import se.faerie.sleep.common.GameBackground.Floor
import se.faerie.sleep.common.GameBackground.GameBackground
import se.faerie.sleep.common.GameBackground.Wall
import se.faerie.sleep.common.GameBackground.Water
import se.faerie.sleep.common.MapPosition

class BlobMapBuilder extends MapBuilder {

  def buildBackground(seed: Long): Array[Array[GameBackground]] = {
    val random = new Random(seed)
    val width = random.nextInt(200) + 100
    val height = random.nextInt(200) + 100
    val backGround = Array.fill(width, height)(Wall)
    val unconnectedRooms = Queue[MapPosition]()
    def insideMapInt(x: Int, y: Int) = x >= 2 && y >= 2 && x < width - 2 && y < height - 2;
    def insideMap(position: MapPosition): Boolean = insideMapInt(position.x, position.y)
    def nrBlobs: Int = (random.nextGaussian() * sqrt(sqrt(width * height)) + sqrt(width * height) / 2).asInstanceOf[Int]

    for (i <- 0 to nrBlobs) {
      val max = random.nextInt(40000) + 20000
      var closed = 0;
      val paint = if(random.nextDouble()>0.3) Floor else Water
      val neighboursToQueue = random.nextInt(6) + 1
      val queue = Queue[MapPosition]()
      val openPropability = 0.7 + random.nextDouble() * 0.25
      val position = new MapPosition(random.nextInt(width), random.nextInt(height))
      queue += position

      if (paint == Floor) {
        unconnectedRooms += position
      }
      while (!queue.isEmpty && closed < max) {
        val position = queue.dequeue
        if (random.nextDouble < openPropability && insideMap(position)) {
          for (a <- -1 to 1) {
            for (b <- -1 to 1) {
              backGround(position.x + a)(position.y + b) = paint
              closed += 1;
            }
          }

          // put x random neighbours in the queue
          for (n <- 0 until neighboursToQueue) {
            queue += position.translate(random.nextInt(5) - 2, random.nextInt(5) - 2)
          }
        }
      }
    }

    // make sure we connect all rooms
    val connectedRooms = ListBuffer[MapPosition]()
    connectedRooms += unconnectedRooms.dequeue
    while (!unconnectedRooms.isEmpty) {
      val roomToConnect = unconnectedRooms.dequeue

      // find closest connected room
      val closestRoom = connectedRooms.reduceLeft((a, b) => (if (a.distanceTo(roomToConnect) < b.distanceTo(roomToConnect)) a else b))

      val tunnelSize = 1+random.nextInt(4)

      val minX = min(roomToConnect.x, closestRoom.x)-tunnelSize
      val maxX = max(roomToConnect.x, closestRoom.x)+tunnelSize
      val minY = min(roomToConnect.y, closestRoom.y)-tunnelSize
      val maxY = max(roomToConnect.y, closestRoom.y)+tunnelSize
      
      // tunnel in x from connect room towards the closest room
      for (x <- minX to maxX) {
        for (a <- -tunnelSize to tunnelSize) {
          if (insideMapInt(x, roomToConnect.y + a)) {
            backGround(x)(roomToConnect.y + a) = Floor
          }
        }
      }

      // tunnel in y from closest room to connect room
      for (y <- minY to maxY) {
        for (a <- -tunnelSize to tunnelSize) {
          if (insideMapInt(closestRoom.x + a, y)) {
            backGround(closestRoom.x + a)(y) = Floor
          }
        }
      }

      // link rooms in y

      // add room to connected list
      connectedRooms += roomToConnect
    }

    return backGround;
  }

}