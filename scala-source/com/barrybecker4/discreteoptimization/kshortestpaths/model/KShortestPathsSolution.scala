package com.barrybecker4.discreteoptimization.kshortestpaths.model

import com.barrybecker4.graph.Path


case class KShortestPathsSolution(totalCost: Double, destination: Int, k: Int, shortestPaths: Seq[Path]) {
  
  override def toString: String = {
    val eol = if (shortestPaths.nonEmpty) "\n" else ""
    val result = s"$totalCost $destination $k" + eol
    result + shortestPaths.map(path => s"${path.weight} ${path.nodes.mkString(" ")}").mkString("\n")
  }
}
