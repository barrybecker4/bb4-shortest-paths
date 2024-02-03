package com.barrybecker4.discreteoptimization.shortestpaths.solver

import com.barrybecker4.common.format.FormatUtil
import com.barrybecker4.discreteoptimization.common.model.graph.Graph
import com.barrybecker4.discreteoptimization.common.model.graph.algorithms.DijkstrasAlgorithm
import com.barrybecker4.discreteoptimization.common.model.graph.directed.DirectedGraph
import com.barrybecker4.discreteoptimization.shortestpaths.model.ShortestPathsSolution

import scala.util.Random

/**
 */
class DijkstrasPathSolver extends ShortestPathsSolver {

  var graph: DirectedGraph = _

  /**
   * Find k shortest paths from source
   * For now, just print the shortest path k times
   */
  def findPaths(graph: DirectedGraph, sourceVertex: Int): ShortestPathsSolution = {

    val pathCalc = new DijkstrasAlgorithm(graph).findShortestPaths(0)

    val pathCosts = Range(0, graph.numVertices).map(v => pathCalc.distToVertex(v))
    val solution = ShortestPathsSolution(pathCosts.sum, pathCosts.toArray)

    solution
  }

}
