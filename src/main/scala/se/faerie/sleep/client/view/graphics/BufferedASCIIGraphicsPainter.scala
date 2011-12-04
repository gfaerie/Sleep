package se.faerie.sleep.client.view.graphics

import scala.swing.Component
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.RenderingHints;
import se.faerie.sleep.common.MapPosition;

class BufferedASCIIGraphicsPainter(renderingInfo : GraphicsRenderingInfo) extends GraphicsPainter {

	val charSize=renderingInfo.font.getSize
	val image = new BufferedImage(renderingInfo.xSize,
			renderingInfo.ySize, BufferedImage.TYPE_INT_RGB)
	var graphics = image.createGraphics
	
	def draw(char: String, color: Color, background:Color, x: Int, y: Int){
		val drawX=x*charSize
		val drawY=(y+1)*charSize
		graphics.setBackground(background)
		graphics.clearRect(drawX, drawY-charSize, charSize,charSize)
		
		graphics.setColor(color)
		graphics.setFont(renderingInfo.font)
		if(char!=null){
			graphics.drawString(char, drawX, drawY)
		}
		
	}
	
	def paint(g: Graphics) {
		g.drawImage(image,renderingInfo.xOffSet,renderingInfo.yOffSet,null)
		graphics = image.createGraphics
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
	}
}
