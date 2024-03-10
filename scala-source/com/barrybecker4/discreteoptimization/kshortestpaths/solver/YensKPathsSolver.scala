package com.barrybecker4.discreteoptimization.kshortestpaths.solver

import com.barrybecker4.common.format.FormatUtil
import com.barrybecker4.discreteoptimization.common.graph.{Graph, Path}
import com.barrybecker4.discreteoptimization.common.graph.algorithms.{KShortestPathsFinder, YensAlgorithm}
import com.barrybecker4.discreteoptimization.common.graph.directed.DirectedGraph
import com.barrybecker4.discreteoptimization.kshortestpaths.model.KShortestPathsSolution
import com.barrybecker4.discreteoptimization.kshortestpaths.solver.YensKPathsSolver

import scala.util.Random


object YensKPathsSolver {
  val BASE_NAME = "yens_kpaths"
}
/**
 */
class YensKPathsSolver(k: Int = 3) extends KShortestPathsSolver {

  /**
   * Find k shortest paths from source
   */
  def findPaths(graph: DirectedGraph, source: Int, destination: Int, k: Int): KShortestPathsSolution = {

    val paths: Seq[Path] = YensAlgorithm(graph).findKShortestPaths(source, destination, k)

    val totalCost = paths.map(path => path.weight).sum
    val solution = KShortestPathsSolution(totalCost, destination, k, paths)

    solution
  }

}
