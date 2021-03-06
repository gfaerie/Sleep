package se.faerie.sleep.map.pathfinding.astar

import org.junit.Test
import org.junit.Assert
import se.faerie.sleep.common.pathfinding.astar.AStarManhattanHeuristic
import se.faerie.sleep.common.pathfinding.astar.AStarPathFinder
import se.faerie.sleep.common.MapPosition

class AStarPathFinderTest {

  val pathFinder = new AStarPathFinder(new AStarManhattanHeuristic)

  @Test
  def testStraightPath {
    val mapSize = 10
    val mapArray = new Array[Array[Boolean]](mapSize + 1, mapSize + 1)

    for (x <- 0 to mapSize; y <- 0 to mapSize; if (x == 0 || y == 0 || y == mapSize || x == mapSize)) {
      mapArray(x)(y) = true
    }

    def insideArea = (x: Int, y: Int) => x > 0 && y > 0 && x < mapSize && y < mapSize;
    def blockFunction = (x: Int, y: Int) => if (!insideArea(x, y) || (mapArray(x)(y))) -1.0 else 1.0;

    val start = new MapPosition(5, 5)
    val end = new MapPosition(5, 8)
    val path = pathFinder.findPath(blockFunction, start, end)
    val expectedPath = new MapPosition(5, 5) :: new MapPosition(5, 6) :: new MapPosition(5, 7) :: new MapPosition(5, 8) :: Nil
    Assert.assertEquals("Path should match", path, expectedPath)
  }

  @Test
  def testNoPath {
    val mapSize = 10
    val mapArray = new Array[Array[Boolean]](mapSize + 1, mapSize + 1)

    for (x <- 0 to mapSize; y <- 0 to mapSize; if (x == 0 || y == 0 || y == mapSize - 1 || x == mapSize - 1 || x == mapSize / 2 || y == mapSize / 2)) {
      mapArray(x)(y) = true
    }

    def insideArea = (x: Int, y: Int) => x > 0 && y > 0 && x < mapSize && y < mapSize;
    def blockFunction = (x: Int, y: Int) => if (!insideArea(x, y) || (mapArray(x)(y))) -1.0 else 1.0;

    val start = new MapPosition(2, 2)
    val end = new MapPosition(7, 7)
    val path = pathFinder.findPath(blockFunction, start, end)
    Assert.assertEquals("Path should be empty", path.size, 0)

  }

  @Test
  def testBlockedStart {
    val mapSize = 10
    val mapArray = new Array[Array[Boolean]](mapSize + 1, mapSize + 1)

    for (x <- 0 to mapSize; y <- 0 to mapSize; if (x == 0 || y == 0 || y == mapSize || x == mapSize)) {
      mapArray(x)(y) = true
    }

    def insideArea = (x: Int, y: Int) => x > 0 && y > 0 && x < mapSize && y < mapSize;
    def blockFunction = (x: Int, y: Int) => if (!insideArea(x, y) || (mapArray(x)(y))) -1.0 else 1.0;
    val start = new MapPosition(5, 5)
    mapArray(5)(5) = true
    val end = new MapPosition(5, 8)
    val path = pathFinder.findPath(blockFunction, start, end)
    Assert.assertEquals("Path should be empty", path.size, 0)

  }

  @Test
  def testBlockedEnd {
    val mapSize = 10
    val mapArray = new Array[Array[Boolean]](mapSize + 1, mapSize + 1)

    for (x <- 0 to mapSize; y <- 0 to mapSize; if (x == 0 || y == 0 || y == mapSize || x == mapSize)) {
      mapArray(x)(y) = true
    }

    def insideArea = (x: Int, y: Int) => x > 0 && y > 0 && x < mapSize && y < mapSize;
    def blockFunction = (x: Int, y: Int) => if (!insideArea(x, y) || (mapArray(x)(y))) -1.0 else 1.0;
    val start = new MapPosition(5, 5)
    val end = new MapPosition(5, 8)
    mapArray(5)(8) = true
    val path = pathFinder.findPath(blockFunction, start, end)
    Assert.assertEquals("Path should be empty", path.size, 0)

  }

  @Test
  def testSimpleMaze {
    val mapSize = 10
    val mapArray = new Array[Array[Boolean]](mapSize + 1, mapSize + 1)

    // draw border
    for (x <- 0 to mapSize; y <- 0 to mapSize; if (x == 0 || y == 0 || y == mapSize || x == mapSize)) {
      mapArray(x)(y) = true
    }

    //###########
    //#.........#
    //#..####...#
    //#..#......#
    //#..#...####
    //#..#.S.#E.#
    //#..#...#..#
    //#..#####..#
    //#.........#
    //#.........#
    //###########
    // draw a simple maze for the pathfinder
    mapArray(2)(6) = true
    mapArray(2)(5) = true
    mapArray(2)(4) = true
    mapArray(2)(3) = true
    mapArray(3)(3) = true
    mapArray(4)(3) = true
    mapArray(5)(3) = true
    mapArray(6)(3) = true
    mapArray(7)(3) = true
    mapArray(7)(4) = true
    mapArray(7)(5) = true
    mapArray(7)(6) = true
    mapArray(7)(7) = true
    mapArray(6)(7) = true
    mapArray(5)(7) = true
    mapArray(4)(7) = true
    mapArray(4)(8) = true
    mapArray(4)(9) = true

    def insideArea = (x: Int, y: Int) => x > 0 && y > 0 && x < mapSize && y < mapSize;
    def blockFunction = (x: Int, y: Int) => if (!insideArea(x, y) || (mapArray(x)(y))) -1.0 else 1.0;

    val start = new MapPosition(5, 5)
    val end = new MapPosition(5, 8)
    val path = pathFinder.findPath(blockFunction, start, end)

    val expectedPath = new MapPosition(5, 5) :: new MapPosition(4, 5) :: new MapPosition(3, 6) :: new MapPosition(2, 7) :: new MapPosition(1, 6) ::
      new MapPosition(1, 5) :: new MapPosition(1, 4) :: new MapPosition(1, 3) :: new MapPosition(2, 2) :: new MapPosition(3, 2) :: new MapPosition(4, 2) ::
      new MapPosition(5, 2) :: new MapPosition(6, 2) :: new MapPosition(7, 2) :: new MapPosition(8, 3) :: new MapPosition(8, 4) :: new MapPosition(8, 5) ::
      new MapPosition(8, 6) :: new MapPosition(8, 7) :: new MapPosition(7, 8) :: new MapPosition(6, 8) :: new MapPosition(5, 8) :: Nil

    Assert.assertEquals("Path should match", path, expectedPath)
  }

}