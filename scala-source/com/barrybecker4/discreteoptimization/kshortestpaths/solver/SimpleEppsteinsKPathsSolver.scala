package com.barrybecker4.discreteoptimization.kshortestpaths.solver

import com.barrybecker4.common.format.FormatUtil
import com.barrybecker4.discreteoptimization.common.graph.algorithms.{KShortestPathsFinder, SimpleEppsteinsAlgorithm}
import com.barrybecker4.discreteoptimization.common.graph.directed.DirectedGraph
import com.barrybecker4.discreteoptimization.common.graph.{Graph, Path}
import com.barrybecker4.discreteoptimization.kshortestpaths.model.KShortestPathsSolution
import com.barrybecker4.discreteoptimization.kshortestpaths.solver.YensKPathsSolver

import scala.util.Random


object SimpleEppsteinsKPathsSolver {
  val BASE_NAME = "simple_eppsteins_kpaths"
}

/**
 * Eppstein's approach to finding shortest paths is faster than Yen's, but it will also find paths with loops.
 * Sometimes you don't want loops.
 */
class SimpleEppsteinsKPathsSolver extends KShortestPathsSolver {

  /** Find k shortest paths from source (including paths with loops)
   */
  def findPaths(graph: DirectedGraph, source: Int, destination: Int, k: Int): KShortestPathsSolution = {

    val paths: Seq[Path] = SimpleEppsteinsAlgorithm(graph).findKShortestPaths(source, destination, k)

    val totalCost = paths.map(path => path.weight).sum
    val solution = KShortestPathsSolution(totalCost, destination, k, paths)

    solution
  }

}
