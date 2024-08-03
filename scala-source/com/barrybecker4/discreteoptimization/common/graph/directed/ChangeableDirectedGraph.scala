package com.barrybecker4.discreteoptimization.common.graph.directed

import com.barrybecker4.discreteoptimization.common.graph.FloatLocation


/**
 * A version of a directed graph that allows you to remove nodes and edges from the original graph.
 * You can also recover nodes and edges after they have been removed.
 */
class ChangeableDirectedGraph(numVertices: Int, edges: IndexedSeq[DirectedEdge], locations: Option[Array[FloatLocation]] = None) 
  extends DirectedGraph(numVertices, edges, locations) {

  def this(graph: DirectedGraph) = {
    this(graph.numVertices, graph.edges, graph.locations)
  }
  
  private var removedNodeSet = collection.immutable.Set[Int]()
  private var removedEdgeSet = collection.immutable.Set[DirectedEdge]()

  def remove(node: Int): Unit = 
    removedNodeSet += node

  def remove(source: Int, dest: Int): Unit = 
    removedEdgeSet += findOrigEdge(source, dest)
  
  def recover(node: Int): Unit = 
    removedNodeSet -= node

  def recover(source: Int, dest: Int): Unit = 
    removedEdgeSet -= findOrigEdge(source, dest)
  
  def findOrigEdge(source: Int, dest: Int): DirectedEdge = {
    val edges = super.outgoingNeighborsOf(source)
    val optEdge = edges.find(edge => edge.destination == dest)
    if (optEdge.isEmpty) throw new IllegalStateException("No edge found from " + source + " to " + dest) 
    else optEdge.get
  }
 
  def recover(): Unit = {
    removedEdgeSet = Set()
    removedNodeSet = Set()
  }

  override def incomingNeighborsOf(node: Int): Set[DirectedEdge] = {
    if (removedNodeSet.contains(node)) Set.empty
    else super.incomingNeighborsOf(node).filter(edge => !removedNodeSet.contains(edge.source) && !removedEdgeSet.contains(edge))
  }

  override def outgoingNeighborsOf(node: Int): Set[DirectedEdge] = {
    if (removedNodeSet.contains(node)) Set.empty 
    else super.outgoingNeighborsOf(node).filter(edge => !removedNodeSet.contains(edge.destination) && !removedEdgeSet.contains(edge))
  }

  def edgeWeight(edge: DirectedEdge): Double =
    if (removedEdgeSet.contains(edge)) Double.MaxValue else edge.weight

  def edge: Seq[DirectedEdge] = edges.filter(edge => !removedEdgeSet.contains(edge))

}
