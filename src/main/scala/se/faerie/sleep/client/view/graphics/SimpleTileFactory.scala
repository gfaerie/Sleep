package se.faerie.sleep.client.view.graphics

import se.faerie.sleep.common.TileGraphics
import se.faerie.sleep.common.GameBackground._
import se.faerie.sleep.common.GraphicsCompressionHelper

class SimpleTileFactory extends TileFactory{

  def getObjectTile(data: TileGraphics, time: Long): String = {
    return data.char + "";
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