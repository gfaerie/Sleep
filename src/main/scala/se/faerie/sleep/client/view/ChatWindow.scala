package se.faerie.sleep.client.view

import scala.swing.event.EditDone
import javax.swing.ScrollPaneConstants
import javax.swing.text.{ StyleContext, StyleConstants }
import javax.swing.JTextPane
import scala.swing._
import java.awt.event.FocusListener
import java.awt.event.FocusEvent

class ChatWindow extends Frame {
  private val chatPanel = new BoxPanel(Orientation.Vertical);
  private val chatPane = setupMessagePane
  val chatField = setupChatField
  private val scrollPane = setupScrollPane(chatPane)

  chatPanel.contents += scrollPane
  chatPanel.contents += chatField

  title = "Chat"
  contents = chatPanel;
  peer.setMinimumSize(new Dimension(200, 261))

  listenTo(chatField)

  peer.addFocusListener(new FocusListener() {
    def focusGained(e: FocusEvent) {
      chatField.requestFocus();
    }
    def focusLost(e: FocusEvent) {}
  });

  private def setupChatField: TextField = {
    val chatField = new TextField(50)
    chatPanel.contents += chatField
    chatField.peer.setMinimumSize(new Dimension(2000, 20))
    chatField.peer.setMaximumSize(new Dimension(2000, 20))
    chatField.peer.setPreferredSize(new Dimension(2000, 20))
    return chatField;
  }

  private def setupMessagePane: MessagePane = {
    val pane = new MessagePane()
    pane.setMinimumSize(new Dimension(200, 200))
    pane.setPreferredSize(new Dimension(200, 200))
    return pane
  }

  private def setupScrollPane(textPane: MessagePane): ScrollPane = {
    val scrollPane = new ScrollPane(Component.wrap(textPane));
    scrollPane.peer.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.peer.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    scrollPane.peer.setMinimumSize(new Dimension(200, 200))
    scrollPane.peer.setPreferredSize(new Dimension(200, 200))
    return scrollPane
  }

  def addMessage(author: String, message: String) {
    chatPane.addMessage(author, message);
  }
}

object ChatWindow {

  def main(args: Array[String]) = {
    Swing.onEDT {
      val window = new ChatWindow
      window.pack();
      window.visible = true;
    };
  }
}