package com.barrybecker4.discreteoptimization.common.graph.algorithms

import com.barrybecker4.discreteoptimization.common.graph.Path
import com.barrybecker4.discreteoptimization.common.graph.directed.{DirectedEdge, DirectedGraph}

import scala.collection.mutable.{ArrayBuffer, PriorityQueue}


/**
 * find the shortest path in a weighted directed graph using Dijkstra's algorithm
 */
trait KShortestPathsFinder {

  /**
   * @return return shortest paths to all other nodes rom the specified source
   */
  def findKShortestPaths(source: Int, destination: Int, k: Int): List[Path]
  
}
