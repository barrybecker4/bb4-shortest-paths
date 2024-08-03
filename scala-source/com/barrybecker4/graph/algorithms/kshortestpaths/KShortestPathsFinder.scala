package com.barrybecker4.graph.algorithms.kshortestpaths

import com.barrybecker4.graph.Path
import com.barrybecker4.graph.directed.{DirectedEdge, DirectedGraph}

import scala.collection.mutable.{ArrayBuffer, PriorityQueue}


/**
 * find the shortest path in a weighted directed graph using Dijkstra's algorithm
 */
trait KShortestPathsFinder {

  /**
   * @return return shortest paths to all other nodes rom the specified source
   */
  def findKShortestPaths(source: Int, destination: Int, k: Int): Seq[Path]
  
}
