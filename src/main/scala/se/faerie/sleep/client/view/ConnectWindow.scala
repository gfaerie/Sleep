package se.faerie.sleep.client.view
import scala.swing.Frame
import scala.swing.BorderPanel
import scala.swing.Component
import scala.swing.BorderPanel.Position._
import scala.swing.GridPanel
import scala.swing.Button
import scala.swing.Label
import scala.swing.TextField
import se.faerie.sleep.client.PlayerCommands.PlayerConnect
import scala.swing.Panel
import scala.swing.SequentialContainer
import javax.swing.SpringLayout
import javax.swing.Spring
import scala.swing.Alignment

class ConnectWindow extends Frame {
  private val serverField = new TextField(15)
  private val nameField = new TextField(15)
  private val button = setupButton
  private val inputs = setupInputs

  contents = new BorderPanel() {
    add(setupInputs, North)
    add(button, South)
  }

  title = "Connect"

  listenTo(button)

  peer.getRootPane().setDefaultButton(button.peer)

  private def setupInputs: Component =
    new GridPanel(2, 2) {
      val serverLabel = new Label("Server Address")
      serverLabel.horizontalAlignment = Alignment.Left
      serverLabel.peer.setLabelFor(serverField.peer)
      val nameLabel = new Label("Player Name")
      nameLabel.horizontalAlignment = Alignment.Left
      nameLabel.peer.setLabelFor(nameField.peer)
      contents += serverLabel
      contents += serverField
      contents += nameLabel
      contents += nameField
    }

  private def setupButton: Button = {
    val button = new Button("Connect")
    button.defaultCapable = true
    return button
  }

  def getInputs = new PlayerConnect(nameField.text, serverField.text)

}