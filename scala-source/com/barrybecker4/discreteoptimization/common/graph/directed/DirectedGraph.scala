package com.barrybecker4.discreteoptimization.common.graph.directed

import com.barrybecker4.discreteoptimization.common.Location


/** 
 * An immutable directed graph with optional node locations
 */
case class DirectedGraph(numVertices: Int, edges: IndexedSeq[DirectedEdge], locations: Option[Array[Location]] = None) {

  // Map from vertex to its neighbors
  private val outgoingNeighborMap: DirectedNeighborMap = DirectedNeighborMap()
  private val incomingNeighborMap: DirectedNeighborMap = DirectedNeighborMap()
  computeNeighborsMap()

  def outgoingNeighborsOf(v: Int): Set[DirectedEdge] = outgoingNeighborMap(v)
  def incomingNeighborsOf(v: Int): Set[DirectedEdge] = incomingNeighborMap(v)

  def findEdge(source: Int, dest: Int): DirectedEdge = {
    val edges = outgoingNeighborsOf(source)
    val optEdge = edges.find(edge => edge.destination == dest)
    if (optEdge.isEmpty) throw new IllegalStateException("No edge found from " + source + " to " + dest)
    else optEdge.get
  }
  private def computeNeighborsMap(): Unit = {
    for (edge <- edges) {
      outgoingNeighborMap.addNeighbor(edge.source, edge)
      incomingNeighborMap.addNeighbor(edge.destination, edge)
    }
  }

}


