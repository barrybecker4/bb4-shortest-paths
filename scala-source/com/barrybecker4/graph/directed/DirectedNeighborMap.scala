package com.barrybecker4.graph.directed

import scala.collection.immutable.Map

/** 
 * Keeps track of node neighbors. There can be more than one edge from a to b.
 */
class DirectedNeighborMap {

  private var map: Map[Int, Set[DirectedEdge]] = Map()

  def apply(v: Int): Set[DirectedEdge] = map.getOrElse(v, Set.empty)

  def addNeighbor(v1: Int, edge: DirectedEdge): Unit = {
    if (map.contains(v1))
      map += v1 -> (map(v1) + edge)
    else
      map += v1 -> Set(edge)
  }
}
