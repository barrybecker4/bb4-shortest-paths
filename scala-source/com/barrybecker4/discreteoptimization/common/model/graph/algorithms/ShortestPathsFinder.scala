package com.barrybecker4.discreteoptimization.common.model.graph.algorithms

import com.barrybecker4.discreteoptimization.common.model.graph.directed.{DirectedEdge, DirectedGraph}
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
