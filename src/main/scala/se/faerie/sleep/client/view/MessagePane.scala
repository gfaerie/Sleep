package se.faerie.sleep.client.view
import scala.swing.Swing

import javax.swing.text.StyleConstants

import javax.swing.text.StyleContext

import javax.swing._
import scala.math._

class MessagePane() extends JTextPane {
  var maxLength = 2000;

  setEditable(false);

  private val (msgStyle, authStyle) = {
    val baseStyle = StyleContext.getDefaultStyleContext().
      getStyle(StyleContext.DEFAULT_STYLE)
    val msgStyle = addStyle("msg", baseStyle)
    val authStyle = addStyle("auth", baseStyle)
    StyleConstants.setBold(authStyle, true)
    (msgStyle, authStyle)
  }

  def addMessage(header: String, msg: String) {

    val doc = getStyledDocument();
    val length = doc.getLength
    if (length > maxLength) {
      var removeIndex = min(doc.getText(0, doc.getLength).indexOf("\n", maxLength / 2), length);
      doc.remove(0, removeIndex + 1);
    }

    val authBuilder = new StringBuilder
    if (doc.getLength != 0) {
      authBuilder.append("\n")
    }
    authBuilder.append(header)
    authBuilder.append(": ")
    doc.insertString(doc.getLength, authBuilder.toString, authStyle)
    doc.insertString(doc.getLength, msg, msgStyle)
    setCaretPosition(doc.getLength - 1);

  }
}