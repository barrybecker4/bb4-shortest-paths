package com.barrybecker4.discreteoptimization.shortestpaths.model

case class ShortestPathsSolution(totalCost: Double, pathCosts: Array[Double]) {
  
  override def toString: String = {
    val result = totalCost + "\n"
    result + pathCosts.mkString(" ")
  }
}
