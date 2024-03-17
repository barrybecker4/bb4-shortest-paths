package com.barrybecker4.discreteoptimization.common.graph.algorithms.kshortestpaths.eppstein

import com.barrybecker4.discreteoptimization.common.graph.algorithms.kshortestpaths.eppstein.DijkstraNode

class ShortestPathTree(var nodes: Map[Int, DijkstraNode] = Map(), val root: Int = 0) {
  
  def this(root: Int) = {
    this(Map(), root)
  }

  def add(newNode: DijkstraNode): Unit = {
    nodes += newNode.idx -> newNode
  }

  def setParentOf(node: Int, parent: Int): Unit = {
    if (!nodes.contains(node)) 
      nodes += node -> DijkstraNode(node)
    nodes(node).setParent(parent)
  }

  def getParentOf(node: Int): Int =
    if (nodes.contains(node)) nodes(node).getParent
    else -1
}