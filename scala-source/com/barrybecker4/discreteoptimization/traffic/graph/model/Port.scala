package com.barrybecker4.discreteoptimization.traffic.graph.model

case class Port(id: Int, angle: Double, radialLength: Int) {
  val angleRad: Double = angle * Math.PI / 180.0
}