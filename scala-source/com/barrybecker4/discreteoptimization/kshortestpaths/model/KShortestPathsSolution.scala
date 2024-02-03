package com.barrybecker4.discreteoptimization.kshortestpaths.model

import com.barrybecker4.discreteoptimization.common.model.graph.Path


case class KShortestPathsSolution(totalCost: Double, destination: Int, k: Int, shortestPaths: List[Path]) {
  
  override def toString: String = {
    val result = s"$totalCost $destination $k\n"
    result + shortestPaths.map(path => s"${path.weight} ${path.nodes.mkString(" ")}").mkString("\n")
  }
}
