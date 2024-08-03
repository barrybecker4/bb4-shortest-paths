package com.barrybecker4.discreteoptimization.shortestpaths.model

import com.barrybecker4.graph.Path


case class ShortestPathsSolution(totalCost: Double, paths: List[Path]) {
  
  override def toString: String = s"$totalCost\n${paths.mkString("\n")}"
    
}
