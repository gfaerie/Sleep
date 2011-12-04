package se.faerie.sleep.common

object GameBackground extends Enumeration {
	
	type GameBackground = GameBackgroundVal	
	case class GameBackgroundVal(solid: Boolean,passable : Boolean) extends Val
    val Wall = GameBackgroundVal(true, false)
    val Floor = GameBackgroundVal(false, true)
    val Water = GameBackgroundVal(false, false) 
    
    def getById(id: Int) : GameBackground= {
		return GameBackground(id).asInstanceOf[GameBackgroundVal];
	}
}