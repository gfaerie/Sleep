package se.faerie.sleep.client.view.graphics
import se.faerie.sleep.client.view.graphics.light._
import se.faerie.sleep.common.GameBackground._
import se.faerie.sleep.common.network.NetworkProtocol.GameUpdate
import se.faerie.sleep.common.MapPosition
import java.awt.Color

class SimpleGraphicsBuilder(lightFactory: LightFactory, tileFactory: TileFactory,backgroundColorBlender : ColorBlender,objectColorBlender : ColorBlender,  xSize: Int, ySize: Int) extends GraphicsBuilder {

  val waterColor = Color.BLUE.getRGB
  val wallColor = Color.BLACK.getRGB
  val groundColor = Color.LIGHT_GRAY.getRGB

  def buildMinimap(backGround: Array[Array[GameBackground]]): MinimapState = {
    val colorData = Array.ofDim[Int](backGround.length, backGround(0).length)
    for (x <- 0 until backGround.length) {
      for (y <- 0 until backGround(0).length) {
        colorData(x)(y) = if (backGround(x)(y) == Floor) groundColor else if (backGround(x)(y) == Wall) wallColor else waterColor;
      }
    }
    return new MinimapState(colorData)

  }

  def buildGraphics(state: GameUpdate, backGround: Array[Array[GameBackground]]): GraphicsState = {
    val frameTime = System.nanoTime
    val xArraySize = 2 * xSize + 1
    val yArraySize = 2 * ySize + 1
    val colorData = Array.ofDim[Int](xArraySize, yArraySize)
    val redData = Array.ofDim[Double](xArraySize, yArraySize)
    val greenData = Array.ofDim[Double](xArraySize, yArraySize)
    val blueData = Array.ofDim[Double](xArraySize, yArraySize)
    val charData = Array.ofDim[String](xArraySize, yArraySize)
    val startX = state.centralPosition.x - xSize;
    val startY = state.centralPosition.y - ySize;

    def arrayPosition(baseX: Int, baseY: Int, relX: Int, relY: Int) = (baseX + relX - startX, baseY + relY - startY)

    def insideArray(x: Int, y: Int) = x >= 0 & x < xArraySize & y >= 0 & y < yArraySize
    def insideGame(x: Int, y: Int) = x >= 0 & x < backGround.length & y >= 0 & y < backGround(0).length

    for (l <- state.lights) {
      val lightX = l._1.x;
      val lightY = l._1.y;
      val lightSource = lightFactory.getLightSource(l._2, l._1.hashCode, frameTime);

      def blockFunction = (xBlock: Int, yBlock: Int) =>
        {
          val arrayPos = arrayPosition(lightX, lightY, xBlock, yBlock)
          val gameX = lightX + xBlock;
          val gameY = lightY + yBlock;
          !insideGame(gameX, gameY) || backGround(gameX)(gameY).solid
        }

      def lightCallback = (x: Int, y: Int, red: Double, green: Double, blue: Double) => {
        val arrayPos = arrayPosition(lightX, lightY, x, y)
        if (insideArray(arrayPos._1, arrayPos._2)) {
          redData(arrayPos._1)(arrayPos._2) += red;
          greenData(arrayPos._1)(arrayPos._2) += green;
          blueData(arrayPos._1)(arrayPos._2) += blue;
        }
      }
      lightSource.castLight(lightCallback, blockFunction, frameTime);
    }

    for (x <- 0 until xArraySize) {
      for (y <- 0 until yArraySize) {
        val gameX = startX + x;
        val gameY = startY + y;
        if (insideGame(gameX, gameY)) {
          val background = backGround(gameX)(gameY)
          charData(x)(y) = tileFactory.getBackgroundTile(background, frameTime)
          colorData(x)(y) = backgroundColorBlender.blend(
            (redData(x)(y), greenData(x)(y), blueData(x)(y)),
            lightFactory.getBackgroundAbsorption(background, gameX + 31 * gameY, frameTime));
        }
      }
    }

    for (o <- state.objects) {
      val position = arrayPosition(o._1.x, o._1.y, 0, 0)
      val x = position._1
      val y = position._2
      charData(x)(y) = tileFactory.getObjectTile(o._2, frameTime)
      colorData(x)(y) = objectColorBlender.blend(
        (redData(x)(y), greenData(x)(y), blueData(x)(y)),
        lightFactory.getObjectAbsorption(o._2, x + 19 * y, frameTime));
    }

    return new GraphicsState(new MapPosition(startX, startY), colorData, charData);
  }

}