package com.barrybecker4.discreteoptimization.common.graph.directed

import com.barrybecker4.discreteoptimization.common.FloatLocation


/** 
 * An immutable directed graph with optional node locations
 */
case class DirectedGraph(numVertices: Int, edges: IndexedSeq[DirectedEdge], locations: Option[Array[FloatLocation]] = None) {

  // Map from vertex to its neighbors
  private val outgoingNeighborMap: DirectedNeighborMap = DirectedNeighborMap()
  private val incomingNeighborMap: DirectedNeighborMap = DirectedNeighborMap()
  computeNeighborsMap()

  def outgoingNeighborsOf(v: Int): Set[DirectedEdge] = outgoingNeighborMap(v)
  def incomingNeighborsOf(v: Int): Set[DirectedEdge] = incomingNeighborMap(v)

  def findMinWeightEdge(source: Int, dest: Int): DirectedEdge = {
    val edges = outgoingNeighborsOf(source).filter(_.destination == dest)
    if (edges.isEmpty)
      throw new IllegalStateException("No edge found from " + source + " to " + dest) 
    else edges.minBy(_.weight)
  }
  
  /** Reverse all the edges in a given directed graph */
  def transpose: DirectedGraph = 
    DirectedGraph(numVertices, edges.map(e => DirectedEdge(e.destination, e.source, e.weight)), locations)
  
  
  private def computeNeighborsMap(): Unit = {
    for (edge <- edges) {
      outgoingNeighborMap.addNeighbor(edge.source, edge)
      incomingNeighborMap.addNeighbor(edge.destination, edge)
    }
  }

}
