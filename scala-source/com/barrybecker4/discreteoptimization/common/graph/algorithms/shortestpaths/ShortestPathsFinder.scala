package com.barrybecker4.discreteoptimization.common.graph.algorithms.shortestpaths

import com.barrybecker4.discreteoptimization.common.graph.directed.{DirectedEdge, DirectedGraph}

import scala.collection.mutable.{ArrayBuffer, PriorityQueue}


/**
 * find the shortest path in a weighted directed graph using Dijkstra's algorithm
 */
trait ShortestPathsFinder {

  /**
   * @return return shortest paths to all other nodes rom the specified source
   */
  def findShortestPaths(source: Int): ShortestPaths
}
