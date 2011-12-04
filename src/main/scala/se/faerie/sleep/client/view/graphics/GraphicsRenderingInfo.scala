package se.faerie.sleep.client.view.graphics

import java.awt.Font;
import se.faerie.sleep.common.MapPosition;

class GraphicsRenderingInfo(val xSize: Int,val ySize: Int, val xOffSet: Int, val yOffSet: Int, val font: Font) {
  
	def toGameCoordinates(screenX : Int, screenY: Int) : MapPosition = {
		val x : Short = ((screenX)/font.getSize).asInstanceOf[Short];
		val y : Short = ((screenY)/font.getSize).asInstanceOf[Short];
		return new MapPosition(x,y)
	}
}