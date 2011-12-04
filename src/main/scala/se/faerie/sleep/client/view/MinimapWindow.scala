package se.faerie.sleep.client.view
import java.awt.image.BufferedImage
import scala.swing.Frame
import java.awt.Color
import scala.swing.Panel
import scala.swing.FlowPanel
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.Image

class MinimapWindow(screenWidth: Int, screenHeight: Int) extends Frame {
  val mainPanel = new FlowPanel()
  mainPanel.peer.setMinimumSize(new Dimension(screenWidth, screenHeight))
  mainPanel.peer.setSize(screenWidth, screenHeight)
  
  contents = mainPanel;
  title = "Minimap"



  def updateMinimap(background: Image, startX: Int, startY: Int, endX: Int, endY: Int, color: Int) {
    val graphics = mainPanel.peer.getGraphics.asInstanceOf[Graphics2D]
    graphics.drawImage(background, 0, 0, null)
    graphics.setColor(new Color(color))
    graphics.drawLine(startX, startY, startX, endY)
    graphics.drawLine(endX, startY, endX, endY)
    graphics.drawLine(startX, startY, endX, startY)
    graphics.drawLine(startX, endY, endX, endY)
  }
}