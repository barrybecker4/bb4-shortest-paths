package com.barrybecker4.discreteoptimization.common.graph.directed

import com.barrybecker4.discreteoptimization.common.Location
import com.barrybecker4.discreteoptimization.common.graph.directed.Nodes


/** 
 * An immutable directed graph with optional node locations
 */
case class DirectedGraph(nodes: Nodes, edges: IndexedSeq[DirectedEdge]) {

  // Map from vertex to its neighbors
  private val outgoingNeighborMap: DirectedNeighborMap = DirectedNeighborMap()
  private val incomingNeighborMap: DirectedNeighborMap = DirectedNeighborMap()
  computeNeighborsMap()
  
  def this(numNodes: Int, edges: IndexedSeq[DirectedEdge]) = this(new Nodes(numNodes), edges)
  
  def numVertices: Int = nodes.numNodes
  def hasLocations: Boolean = nodes.hasLocations
  def getLocation(nodeIdx: Int): Location = nodes.locations.get(nodeIdx)
  def getNodeWeight(nodeIdx: Int): Double = nodes.getWeight(nodeIdx)

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
    DirectedGraph(nodes, edges.map(e => DirectedEdge(e.destination, e.source, e.weight)))
  
  
  private def computeNeighborsMap(): Unit = {
    for (edge <- edges) {
      outgoingNeighborMap.addNeighbor(edge.source, edge)
      incomingNeighborMap.addNeighbor(edge.destination, edge)
    }
  }

}
