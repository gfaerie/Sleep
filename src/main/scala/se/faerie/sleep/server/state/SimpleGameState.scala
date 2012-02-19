package se.faerie.sleep.server.state

import se.faerie.sleep.common.MapPosition
import collection.mutable.Map
import se.faerie.sleep.common.GameBackground._
import java.util.NoSuchElementException
import scala.collection.mutable.HashMap
import scala.collection.mutable.Set
import scala.collection.mutable.MultiMap
import scala.math._

class SimpleGameState(mapId: Long, backGround: Array[Array[GameBackground]], gridSize: Short) extends GameState {
  val id = mapId;
  val width = backGround.length;
  val height = backGround(0).length;
  private val emptySet = scala.collection.immutable.Set[GameObject]();
  private val sourceMap = Map[Long, MapPosition]();
  private val objectMap = Map[Long, GameObject]();
  private val widthGrid: Int = ceil(width.asInstanceOf[Double] / gridSize.asInstanceOf[Double]).toInt
  private val heightGrid: Int = ceil(height.asInstanceOf[Double] / gridSize.asInstanceOf[Double]).toInt
  private val gridMaps = Array.ofDim[MultiMap[MapPosition, GameObject]](widthGrid, heightGrid);
  private val metadataIndex = new HashMap[Class[_], collection.mutable.Set[GameObject]] with MultiMap[Class[_], GameObject];

  def getAllObjects(): Traversable[GameObject] = objectMap.values

  def addObject(position: MapPosition, source: GameObject) {
    if (!insideGame(position.x, position.y)) {
      throw new InvalidGameStateException("Position " + position + " is outside map")
    }
    for (metadata <- source.staticMetadata) {
      metadataIndex.addBinding(metadata.getClass, source)
    }
    addGridMapping(position, source)
    sourceMap += (source.id -> position);
    objectMap += (source.id -> source);
  }

  def moveObject(objectId: Long, position: MapPosition) {
    if (insideGame(position.x, position.y)) {
      val gameObject = objectMap(objectId);
      val oldPosition = sourceMap(objectId);
      removeGridMapping(oldPosition, gameObject);
      addGridMapping(position, gameObject);
      sourceMap += (objectId -> position);
    }
    else{
      throw new InvalidGameStateException("Position " + position + " is outside map")
    }
    
  };

  def removeObject(objectId: Long) {
    val position = sourceMap(objectId);
    val gameObject = objectMap(objectId);
    for (metadata <- gameObject.staticMetadata) {
      metadataIndex.removeBinding(metadata.getClass, gameObject)
    }
    removeGridMapping(position, gameObject);
    sourceMap -= objectId;
    objectMap -= objectId;
  };

  def objectExists(objectId: Long) = objectMap.contains(objectId)

  def getObject(objectId: Long) = objectMap(objectId);

  def getObjectsAtPosition(position: MapPosition): Traversable[GameObject] = {
    val grid = getMapGrid(position.x, position.y);
    val positionMap = gridMaps(grid._1)(grid._2);
    if (positionMap != null) {
      positionMap.get(position) match {
        case None => return emptySet;
        case Some(set) => return set;
      }
    } else {
      return emptySet;
    }
  }

  def getObjectPosition(objectId: Long) = sourceMap(objectId);

  def getBackground(x: Int, y: Int) = backGround(x)(y);

  def insideGame(x: Int, y: Int): Boolean = return x >= 0 && y >= 0 && x < width && y < height;

  def getObjects(metadata: Class[_]): Traversable[GameObject] = metadataIndex.get(metadata) match {
    case Some(o) => return o;
    case None => return Set.empty
  }

  def getObjects(xStart: Int, yStart: Int, xEnd: Int, yEnd: Int): MultiMap[MapPosition, GameObject] = {
    val blockXStart = if (xStart < 0) 0 else xStart;
    val blockYStart = if (yStart < 0) 0 else yStart;
    val blockXEnd = if (xEnd >= width) width - 1 else xEnd;
    val blockYEnd = if (yEnd >= height) height - 1 else yEnd;

    val map = new HashMap[MapPosition, collection.mutable.Set[GameObject]] with MultiMap[MapPosition, GameObject];
    val startGrid = getMapGrid(blockXStart, blockYStart);
    val endGrid = getMapGrid(blockXEnd, blockYEnd);
    for (y <- startGrid._2 to endGrid._2) {
      for (x <- startGrid._1 to endGrid._1) {
        var gridMap = gridMaps(x)(y);
        if (gridMap != null) {
          map ++= gridMap.filter(e => {
            (e._1.x >= xStart && e._1.x <= xEnd && e._1.y >= yStart && e._1.y <= yEnd)
          });
        };
      };
    };
    return map;
  }

  private def getMapGrid(x: Int, y: Int): (Int, Int) = (x / gridSize, y / gridSize);

  private def addGridMapping(position: MapPosition, gameObject: GameObject) {
    val grid = getMapGrid(position.x, position.y);
    var map = gridMaps(grid._1)(grid._2);
    if (map == null) {
      map = new HashMap[MapPosition, collection.mutable.Set[GameObject]] with MultiMap[MapPosition, GameObject];
      gridMaps(grid._1)(grid._2) = map;
    }
    map.addBinding(position, gameObject);
  }

  private def removeGridMapping(position: MapPosition, gameObject: GameObject) {
    val grid = getMapGrid(position.x, position.y);
    var map = gridMaps(grid._1)(grid._2);
    if (map != null) {
      map.removeBinding(position, gameObject);
      if (map.isEmpty) {
        gridMaps(grid._1)(grid._2) = null;
      }
    }

  }
}