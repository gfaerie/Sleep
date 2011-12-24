package se.faerie.sleep.server.state

import scala.collection.mutable.MultiMap

import se.faerie.sleep.common.GameBackground.GameBackground
import se.faerie.sleep.common.MapPosition

trait GameState {
	def id : Long
	def getObjects(xStart: Int, yStart: Int, xEnd : Int, yEnd: Int): MultiMap[MapPosition, GameObject]
	def getAllObjects(): Traversable[GameObject]
	def getObjects(metadata : GameObjectMetadata): Traversable[GameObject]
	def removeObject(objectId: Long)
	def objectExists(objectId: Long): Boolean;
	def moveObject(objectId: Long,position: MapPosition)
	def getObject(objectId: Long) : GameObject
	def getObjectsAtPosition(position: MapPosition) : Traversable[GameObject]
	def getObjectPosition(objectId: Long): MapPosition
	def getBackground(x: Int, y: Int): GameBackground
	def insideGame(x: Int, y: Int): Boolean
	def width: Int
	def height: Int
	def addObject(position: MapPosition, source: GameObject)
}