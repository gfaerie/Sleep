package se.faerie.sleep.client.view

import scala.swing.Frame
import java.awt.Dimension
import javax.swing.JFrame
import scala.swing.Component
import scala.swing.Panel
import java.awt.event.FocusListener
import java.awt.event.FocusEvent
import scala.swing.MenuBar
import scala.swing.Menu
import scala.swing.MenuItem
import scala.swing.Action
import scala.swing.Separator
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import scala.swing.FlowPanel

class MainWindow(screenWidth: Int, screenHeight: Int) extends Frame {
  val mainPanel = new Panel {
    listenTo(mouse.clicks)
    listenTo(keys)
  }
  mainPanel.peer.setMinimumSize(new Dimension(screenWidth, screenHeight))
  mainPanel.peer.setSize(screenWidth, screenHeight)
  
  contents = mainPanel;

  title = "Game";

  peer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)

  peer.addFocusListener(new FocusListener() {
    def focusGained(e: FocusEvent) {
      mainPanel.requestFocus();
    }
    def focusLost(e: FocusEvent) {}
  });
  mainPanel.requestFocus();
}