package se.faerie.sleep.common

trait GraphicsCompressionHelper {

  def storeSphericalLight(intensity: Byte, r: Byte, g: Byte, b: Byte): Int = {
    return ((intensity & 0xFF) << 24) |
      ((r & 0xFF) << 16) |
      ((g & 0xFF) << 8) |
      ((b & 0xFF) << 0);
  }

  def loadSphericalLight(id: Int): (Byte, Byte, Byte, Byte) = {
    return (((id >> 24) & 0xff).asInstanceOf[Byte],
      ((id >> 16) & 0xFF).asInstanceOf[Byte],
      ((id >> 8) & 0xFF).asInstanceOf[Byte],
      ((id >> 0) & 0xFF).asInstanceOf[Byte]);
  }

  def storeGraphics(char: Char, r: Byte, g: Byte, b: Byte): Int = {
    return ((char.asInstanceOf[Byte] & 0xFF) << 24) |
      ((r & 0xFF) << 16) |
      ((g & 0xFF) << 8) |
      ((b & 0xFF) << 0);
  }

  def loadGraphics(id: Int): (Char, Byte, Byte, Byte) = {
    return (((id >> 24) & 0xff).asInstanceOf[Char], 
        ((id >> 16) & 0xFF).asInstanceOf[Byte], 
        ((id >> 8) & 0xFF).asInstanceOf[Byte], 
        ((id >> 0) & 0xFF).asInstanceOf[Byte]);
  }

}