package com.barrybecker4.discreteoptimization.common.model

case class IntLocation(x: Int, y: Int) {

  // manhattan distance
  def distance(location: IntLocation): Int = {
    val deltaX = location.x - x
    val deltaY = location.y - y
    Math.abs(deltaX) + Math.abs(deltaY)
  }
}
