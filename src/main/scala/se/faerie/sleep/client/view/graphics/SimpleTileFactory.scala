package se.faerie.sleep.client.view.graphics

import se.faerie.sleep.common.GameBackground._
import se.faerie.sleep.common.GraphicsCompressionHelper

class SimpleTileFactory extends TileFactory with GraphicsCompressionHelper{

  def getObjectTile(id: Int, time: Long): String = {
    return loadGraphics(id)._1 + "";
  }

  def getBackgroundTile(background: GameBackgroundVal, time: Long): String = {
    background match {
      case Wall => {
        return "#";
      }
      case Water => {
        return "~";
      }
      case Floor => {
        return ".";
      }
      case _ => return "";
    }
  }

}