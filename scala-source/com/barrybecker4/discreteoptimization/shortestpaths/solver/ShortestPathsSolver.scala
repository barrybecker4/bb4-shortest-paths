package com.barrybecker4.discreteoptimization.shortestpaths.solver

import com.barrybecker4.graph.directed.DirectedGraph
import com.barrybecker4.discreteoptimization.shortestpaths.model.ShortestPathsSolution

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
