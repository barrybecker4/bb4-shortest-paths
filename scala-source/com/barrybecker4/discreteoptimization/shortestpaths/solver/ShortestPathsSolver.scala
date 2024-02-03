package com.barrybecker4.discreteoptimization.shortestpaths.solver

import com.barrybecker4.discreteoptimization.shortestpaths.model.ShortestPathsSolution
import com.barrybecker4.discreteoptimization.common.model.graph.directed.DirectedGraph

/**
 * Consider Dijkstra's or Bellman-ford algorithms
 *
 *  For visualization look at
 *   - JGraphT - https://jgrapht.org/ - generate viz and save as images
 *   - GraphStream - https://graphstream-project.org/
 */
trait ShortestPathsSolver {

    def findPaths(graph: DirectedGraph, sourceVertex: Int = 0): ShortestPathsSolution

}
