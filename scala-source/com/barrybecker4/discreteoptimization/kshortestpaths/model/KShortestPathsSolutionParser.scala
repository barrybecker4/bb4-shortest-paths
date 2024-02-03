package com.barrybecker4.discreteoptimization.kshortestpaths.model

import com.barrybecker4.discreteoptimization.common.model.graph.Path
import com.barrybecker4.discreteoptimization.kshortestpaths.model.KShortestPathsSolution


case class KShortestPathsSolutionParser() {

  def parse(lines: IndexedSeq[String]): KShortestPathsSolution = {
    // parse the data in the file
    val firstLine = lines(0).split("\\s+")
    val totalCost = firstLine(0).toDouble
    val destination = firstLine(1).toInt
    val k = firstLine(2).toInt
    
    val pathLines = lines.tail
    val paths: List[Path] = pathLines.map(line => {
      val parts = line.split(" ")
      Path(parts.head.toDouble, parts.tail.map(_.toInt).toList)
    }).toList
    
    KShortestPathsSolution(totalCost, destination, k, paths)
  }
}
