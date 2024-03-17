package com.barrybecker4.discreteoptimization.common.graph.algorithms.kshortestpaths.eppstein

import com.barrybecker4.discreteoptimization.common.graph.directed.DirectedEdge


class DijkstraNode(var idx: Int, 
                   var dist: Double = Double.MaxValue, 
                   var depth: Int = 0,
                   var neighbors: Map[Int, Double] = Map()) extends Comparable[DijkstraNode] {

  def addEdge(toNode: Int, weight: Double): Unit =
    neighbors += toNode -> weight

  def removeEdge(toNode: Int): Double = {
    if (neighbors.contains(toNode)) {
      val weight: Double = neighbors(toNode)
      neighbors -= toNode
      weight
    } else {
      Double.MaxValue
    }
  }

  def getAdjacencyList: Set[Int] = neighbors.keySet

  def getEdges: List[DirectedEdge] = {
    var edges: List[DirectedEdge] = List()
    for (toNode <- neighbors.keySet) {
      edges +:= DirectedEdge(idx, toNode, neighbors(toNode))
    }
    edges
  }

  def setParent(parent: Int): Unit = {
    neighbors = Map()
    neighbors += parent -> 0.0
  }

  def getParent: Int = {
    val neighborIndices = neighbors.keySet
    if (neighborIndices.size > 1) -1
    else if (neighborIndices.size < 1)  -1
    else neighbors.keySet.iterator.next
  }

  override def compareTo(comparedNode: DijkstraNode): Int = {
    val distance1 = this.dist
    val distance2 = comparedNode.dist
    if (distance1 == distance2) 0
    else if (distance1 > distance2) 1
    else  - 1
  }

  def equals(comparedNode: DijkstraNode): Boolean = this.idx.equals(comparedNode.idx)
}