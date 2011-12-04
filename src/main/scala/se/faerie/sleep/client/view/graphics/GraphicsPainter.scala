package se.faerie.sleep.client.view.graphics

import scala.swing.UIElement
import java.awt.Graphics2D
import java.awt.{Color, Graphics};
import se.faerie.sleep.common.MapPosition;

trait GraphicsPainter{
  def draw(graphics: String, color: Color, background:Color, x: Int, y: Int);
  def paint(g: Graphics);
}
