package se.faerie.sleep.client.view

import java.awt.image.BufferedImage
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Point
import java.awt.RenderingHints
import java.awt.Toolkit
import scala.swing.event.Key.Enter
import scala.swing.event.ButtonClicked
import scala.swing.event.EditDone
import scala.swing.event.KeyPressed
import scala.swing.event.MousePressed
import scala.swing.Action
import scala.swing.Menu
import scala.swing.MenuBar
import scala.swing.MenuItem
import scala.swing.Separator
import scala.swing.Swing
import java.awt.image.BufferedImage
import graphics.GraphicsRenderingInfo
import se.faerie.sleep.client.PlayerCommands._
import se.faerie.sleep.client.view.graphics.GraphicsState
import java.awt.Dimension
import se.faerie.sleep.client.view.graphics.MinimapState
import java.awt.Image
import java.awt.Insets

/**
 *
 * 	Class is a mess
 *
 * TODO clean up dependencies between view classes
 *
 */
class SimpleView(renderingInfo: GraphicsRenderingInfo) extends View {

  private val charSize = renderingInfo.font.getSize
  private val image = new BufferedImage(renderingInfo.xSize,
    renderingInfo.ySize, BufferedImage.TYPE_INT_RGB)
  private var graphics = image.createGraphics
  private val chatWindow = new ChatWindow()
  private val logWindow = new LogWindow()
  private val connectWindow = new ConnectWindow()
  private val minimapWindow = new MinimapWindow(375, 375)
  private val mainWindow = new MainWindow(renderingInfo.xSize, renderingInfo.ySize)
  private var lastFrame: GraphicsState = null
  private var miniMap: Image = null

  logWindow.menuBar = new MenuBar {
    contents += new Menu("File") {
      contents += new MenuItem(Action("Connect") {
        connectWindow.visible = true;
      })
      contents += new Separator
      contents += new MenuItem(Action("Quit") {
        playerActionHandler ! new PlayerShutdown(5000);
      })
    }
  }

  connectWindow.reactions += {

    case ButtonClicked(b) => {
      val connect = connectWindow.getInputs
      if (connect.serverAddress != null && connect.name != null && connect.serverAddress.length > 0 && connect.name.length() > 0) {
        playerActionHandler ! connect
        connectWindow.visible = false;
      }
    }
  }

  chatWindow.reactions += {
    case EditDone(t) => {
      var message = t.peer.getText;
      if (message.length() > 0) {
        playerActionHandler ! new PlayerChat(message)
        t.peer.setText("")
      }
    }
  }

  mainWindow.mainPanel.reactions += {
    case m: MousePressed => {
      if (lastFrame != null) {
        val clicked = renderingInfo.toGameCoordinates(m.point.x, m.point.y)
        val upperLeft = lastFrame.upperRight
        val target = upperLeft.translate(clicked)
        playerActionHandler ! new PlayerAction(m.peer.getButton, target.x, target.y)
      }
    }
    case k: KeyPressed => {
      if (k.key equals Enter) {
        chatWindow.chatField.requestFocus()
      }
    }
  }

  Swing.onEDT {
    chatWindow.pack();
    logWindow.pack();
    connectWindow.pack();
    connectWindow.location = new Point(200, 200);
    logWindow.location = new Point(0, 0);
    chatWindow.location = new Point(logWindow.size.getWidth.intValue, 0);
    mainWindow.location = new Point(0, logWindow.size.getHeight.intValue);
    minimapWindow.location = new Point(logWindow.size.getWidth.intValue + chatWindow.size.getWidth.intValue, 0);
    chatWindow.visible = true;
    logWindow.visible = true;
  };

  def showChat(author: String, message: String) = {
    chatWindow.addMessage(author, message);
  }

  def showLog(message: String) = {
    logWindow.addMessage(message);
  }

  def showMinimap(update: MinimapState) = {
    val miniXSize = update.map.length
    val miniYSize = update.map(0).length
    val image = new BufferedImage(miniXSize, miniYSize, BufferedImage.TYPE_INT_ARGB)
    for (x <- 0 until miniXSize) {
      for (y <- 0 until miniYSize) {
        image.setRGB(x, y, update.map(x)(y))
      }
    }
    this.miniMap = image;
    val insets = minimapWindow.peer.getInsets();
    val insetwidth = insets.left + insets.right;
    val insetheight = insets.top + insets.bottom;
    minimapWindow.peer.setMinimumSize(new Dimension(miniXSize + insetwidth, miniYSize + insetheight))
    minimapWindow.peer.setSize(miniXSize + insetwidth, miniYSize + insetheight)
    minimapWindow.peer.validate
    minimapWindow.pack.open;
  }

  // this method should be moved to mainwindow
  def showGraphics(currentFrame: GraphicsState) = {
    val firstDraw = lastFrame == null;
    for (y <- 0 to currentFrame.chars.size - 1) {
      for (x <- 0 to currentFrame.chars(0).size - 1) {
        if ((lastFrame == null) ||
          (currentFrame.chars(x)(y) == null) ||
          (!(currentFrame.chars(x)(y) equals lastFrame.chars(x)(y))) ||
          (!(currentFrame.colors(x)(y) equals lastFrame.colors(x)(y)))) {
          draw(currentFrame.chars(x)(y), new Color(currentFrame.colors(x)(y)), Color.BLACK, x, y);
        }
      }
    }
    lastFrame = currentFrame

    val frameGraphics: Graphics2D = mainWindow.mainPanel.peer.getGraphics.asInstanceOf[Graphics2D];
    frameGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
    frameGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    frameGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
    frameGraphics.drawImage(image, renderingInfo.xOffSet, renderingInfo.yOffSet, null)
    if (firstDraw) {
      val insets = mainWindow.peer.getInsets();
      val insetwidth = insets.left + insets.right;
      val insetheight = insets.top + insets.bottom;
      mainWindow.peer.setMinimumSize(new Dimension(renderingInfo.xSize + insetwidth,
        renderingInfo.ySize + insetheight))
      mainWindow.peer.setSize(renderingInfo.xSize + insetwidth,
        renderingInfo.ySize + insetheight)
      mainWindow.peer.validate
      mainWindow.pack.open
    }

    minimapWindow.updateMinimap(miniMap, currentFrame.upperRight.x, currentFrame.upperRight.y, currentFrame.upperRight.x + currentFrame.chars.length, currentFrame.upperRight.y + currentFrame.chars(0).length, Color.RED.getRGB)
    Toolkit.getDefaultToolkit().sync();
  }

  // this method should be moved to mainwindow
  def draw(char: String, color: Color, background: Color, x: Int, y: Int) {
    val drawX = x * charSize
    val drawY = (y + 1) * charSize
    graphics.setBackground(background)
    graphics.clearRect(drawX, drawY - charSize, charSize, charSize)

    graphics.setColor(color)
    graphics.setFont(renderingInfo.font)
    if (char != null) {
      graphics.drawString(char, drawX, drawY)
    }
  }
}
