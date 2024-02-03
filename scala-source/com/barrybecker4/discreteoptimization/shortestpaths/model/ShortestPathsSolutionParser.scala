package com.barrybecker4.discreteoptimization.shortestpaths.model

import com.barrybecker4.discreteoptimization.shortestpaths.model.ShortestPathsSolution


case class ShortestPathsSolutionParser() {

  def parse(lines: IndexedSeq[String]): ShortestPathsSolution = {
    // parse the data in the file
    val firstLine = lines(0).split("\\s+")
    val totalCost = firstLine(0).toDouble
    
    val pathCosts = lines(1).split(" ").map(_.toDouble)
    
    ShortestPathsSolution(totalCost, pathCosts)
  }
}
