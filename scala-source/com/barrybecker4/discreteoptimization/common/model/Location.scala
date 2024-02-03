package com.barrybecker4.discreteoptimization.common.model

case class Location(x: Float, y: Float) {
  
  def distance(location: Location): Double = {
    val deltaX = location.x - x
    val deltaY = location.y - y
    Math.sqrt(deltaX * deltaX + deltaY * deltaY)
  }
  
  def midPoint(location: Location): Location = {
    Location((location.x + x) / 2, (location.y + y) / 2)
  }
}

object Location {
  def apply(x: Int, y: Int): Location = new Location(x.toFloat, y.toFloat)
  
  def centroid(locations: Seq[Location]): Location = {
    val x = locations.map(_.x).sum / locations.size
    val y = locations.map(_.y).sum / locations.size
    Location(x, y)
  }
}
