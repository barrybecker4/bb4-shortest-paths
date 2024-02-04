package com.barrybecker4.discreteoptimization.kshortestpaths.solver

import com.barrybecker4.common.format.FormatUtil
import com.barrybecker4.discreteoptimization.common.graph.{Graph, Path}
import com.barrybecker4.discreteoptimization.common.graph.algorithms.{DijkstrasAlgorithm, YensAlgorithm}
import com.barrybecker4.discreteoptimization.common.graph.directed.DirectedGraph
import com.barrybecker4.discreteoptimization.kshortestpaths.model.KShortestPathsSolution
import com.barrybecker4.discreteoptimization.kshortestpaths.solver.GreedyKPathsSolver

import scala.util.Random

/**
 */
class GreedyKPathsSolver(k: Int = 3) extends KShortestPathsSolver {

  var graph: DirectedGraph = _

  /**
   * Find k shortest paths from source
   * For now, just print the shortest path k times
   */
  def findPaths(graph: DirectedGraph, source: Int, destination: Int, k: Int): KShortestPathsSolution = {

    val paths: List[Path] = new YensAlgorithm(graph).findKShortestPaths(source, destination, k)

    val totalCost = paths.map(path => path.weight).sum
    val solution = KShortestPathsSolution(totalCost, destination, k, paths)

    solution
  }

}
