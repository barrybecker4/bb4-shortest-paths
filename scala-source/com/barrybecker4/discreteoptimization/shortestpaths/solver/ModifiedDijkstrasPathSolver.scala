package com.barrybecker4.discreteoptimization.shortestpaths.solver

import com.barrybecker4.common.format.FormatUtil
import com.barrybecker4.discreteoptimization.common.graph.{Graph, Path}
import com.barrybecker4.discreteoptimization.common.graph.algorithms.{DijkstrasAlgorithm, ModifiedDijkstrasAlgorithm}
import com.barrybecker4.discreteoptimization.common.graph.directed.DirectedGraph
import com.barrybecker4.discreteoptimization.shortestpaths.model.ShortestPathsSolution

import scala.util.Random

/**
 */
class ModifiedDijkstrasPathSolver extends ShortestPathsSolver {

  var graph: DirectedGraph = _

  /**
   * Find k shortest paths from source
   * For now, just print the shortest path k times
   */
  def findPaths(graph: DirectedGraph, sourceVertex: Int): ShortestPathsSolution = {

    val alg = new ModifiedDijkstrasAlgorithm(graph)

    val pathCosts = Range(0, graph.numVertices)
      .map(v => alg.getShortestPath(0, v).getOrElse(Path(Double.PositiveInfinity, List())).weight)
    val solution = ShortestPathsSolution(pathCosts.sum, pathCosts.toArray)

    solution
  }

}
