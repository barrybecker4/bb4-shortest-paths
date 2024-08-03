package com.barrybecker4.discreteoptimization.shortestpaths.model

import com.barrybecker4.graph.Path
import com.barrybecker4.discreteoptimization.shortestpaths.model.ShortestPathsSolution


case class ShortestPathsSolutionParser() {

  def parse(lines: IndexedSeq[String]): ShortestPathsSolution = {
    val firstLine = lines(0).split("\\s+")
    val totalCost = firstLine(0).toDouble
    val paths = lines.drop(1).map(new Path(_)).toList
    ShortestPathsSolution(totalCost, paths)
  }

}
    