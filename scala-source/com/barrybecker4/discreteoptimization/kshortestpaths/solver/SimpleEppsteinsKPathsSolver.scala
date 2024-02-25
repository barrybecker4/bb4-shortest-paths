package com.barrybecker4.discreteoptimization.kshortestpaths.solver

import com.barrybecker4.common.format.FormatUtil
import com.barrybecker4.discreteoptimization.common.graph.algorithms.{KShortestPathsFinder, SimpleEppsteinsAlgorithm}
import com.barrybecker4.discreteoptimization.common.graph.directed.DirectedGraph
import com.barrybecker4.discreteoptimization.common.graph.{Graph, Path}
import com.barrybecker4.discreteoptimization.kshortestpaths.model.KShortestPathsSolution
import com.barrybecker4.discreteoptimization.kshortestpaths.solver.GreedyKPathsSolver

import scala.util.Random

/**
 */
class SimpleEppsteinsKPathsSolver(k: Int = 3) extends KShortestPathsSolver {


  /**
   * Find k shortest paths from source
   */
  def findPaths(graph: DirectedGraph, source: Int, destination: Int, k: Int): KShortestPathsSolution = {

    val paths: List[Path] = SimpleEppsteinsAlgorithm(graph).findKShortestPaths(source, destination, k)

    val totalCost = paths.map(path => path.weight).sum
    val solution = KShortestPathsSolution(totalCost, destination, k, paths)

    solution
  }

}
