package se.faerie.sleep.client.view.graphics

trait ColorBlender {

   def blend(colors: (Double, Double, Double), abs: (Double, Double, Double)): Int;
  
}