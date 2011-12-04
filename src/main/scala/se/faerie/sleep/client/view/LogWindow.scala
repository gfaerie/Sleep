package se.faerie.sleep.client.view

import scala.swing._
import javax.swing.ScrollPaneConstants
import java.text.SimpleDateFormat
import java.util.Date

class LogWindow extends Frame {

  title = "Client"
  peer.setMinimumSize(new Dimension(216, 250))
  val logPane = setupLogPane
  contents = setupScrollPane(logPane)
  val dateFormat = new SimpleDateFormat("HH:mm:ss");
  
  private def setupLogPane: MessagePane = {
    val textArea = new MessagePane()
    textArea.setMinimumSize(new Dimension(200, 200))
    textArea.setPreferredSize(new Dimension(200, 200))
    return textArea
  }

  private def setupScrollPane(textPane: MessagePane): ScrollPane = {
    val scrollPane = new ScrollPane(Component.wrap(textPane));
    scrollPane.peer.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.peer.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    scrollPane.peer.setMinimumSize(new Dimension(200, 200))
    scrollPane.peer.setPreferredSize(new Dimension(200, 200))
    return scrollPane
  }
  
   def addMessage(message : String){
     logPane.addMessage(dateFormat.format(new Date()),message);
  }
}