package com.barrybecker4.discreteoptimization.common.graph.algorithms

import com.barrybecker4.discreteoptimization.common.graph.{Edge, Path}
import com.barrybecker4.discreteoptimization.common.graph.directed.{DirectedEdge, DirectedGraph}


/**
 * Data structure for representing a source-target path implicitly inside the priority queue of candidate k shortest
 * paths during the execution of the simplified version of Eppstein's algorithm.
 *
 * @param sidetrackEdge last sidetrack edge in this candidate path
 * @param parentPath index of the shorter path that this path sidetracks from
 * @param cost the total cost of the path
 */
class ImplicitPath(var sidetrackEdge: DirectedEdge, graph: DirectedGraph,
                   var parentPath: Int, var cost: Double) extends Comparable[ImplicitPath] {

  /**
   * Convert from the implicit representation of the path to an explicit listing of all of the edges in the path
   * There are potentially three pieces to the path:
   * 1) the path from node s (source) to node u in the parent path
   * 2) the sidetrack edge (u,v)
   * 3) the shortest path (in the shortest path tree) from node v to node t (target)
   */
  def explicitPath(ksp: Seq[Path], tree: ShortestPaths): Path = {
    var explicitPath: Path = new Path(0, List(sidetrackEdge.destination))
    // If path is not the shortest path in the graph...
    if (parentPath >= 0) {
      // Get the explicit representation of the shorter parent path that this path sidetracks from
      val explicitPrefPath = ksp(parentPath)
      // 1a) Identify the s-u portion of the path
      // Identify and add the segment of the parent path up until the point where current path sidetracks off of it.
      // In other words, if (u, v) is the sidetrack edge of the current path off of the parent path, look for the
      // last instance of node u in the parent path.
      val edges = getPathEdges(explicitPrefPath)
      var lastEdgeNum = -1
      var idx = edges.size - 1
      var done = false
      while (idx >= 0 && !done) {
        val currentEdge = edges(idx)
        if (currentEdge.destination.equals(sidetrackEdge.source)) {
          lastEdgeNum = idx
          done = true
        }
        idx -= 1
      }
      // 1b) Add the s-u portion of the path
      // Copy the explicit parent path up to the identified point where the current/child path sidetracks
      var explicitNodes = List(explicitPrefPath.nodes.head)
      var weight: Double = 0
      for (i <- 0 to lastEdgeNum) {
        explicitNodes :+= edges(i).destination
        weight += edges(i).weight
      }
      // 2) Add the (u, v) portion of the path
      // Add the last sidetrack edge to the explicit path representation
      explicitNodes :+= sidetrackEdge.destination
      explicitPath = Path(weight + sidetrackEdge.weight, explicitNodes)
    }

    // 3) Add the v-t portion of the path
    // Add the shortest path from v (either the source node, or the incoming node of the sidetrack edge associated
    // with the current path) to the explicit path representation
    var current = sidetrackEdge.destination
    var vtWeight: Double = 0
    var vtNodes: List[Int] = List()
    while (current != tree.source) {
      val next: Int = tree.previousNode(current).get
      vtWeight += (tree.distToVertex(current) - tree.distToVertex(next))
      vtNodes :+= next
      current = next
    }
    explicitPath = Path(explicitPath.weight + vtWeight, explicitPath.nodes ++ vtNodes)
    explicitPath
  }

  private def getPathEdges(path: Path): List[DirectedEdge] = {
    val iterator = path.nodes.iterator
    var currentNode = iterator.next()
    var nextNode = 0
    var edges: List[DirectedEdge] = List()
    while (nextNode != path.lastNode) {
      nextNode = iterator.next()
      edges :+= graph.findEdge(currentNode, nextNode)
      currentNode = nextNode
    }
    edges
  }

  override def compareTo(comparedNode: ImplicitPath): Int = {
    val cost1 = this.cost
    val cost2 = comparedNode.cost
    if (cost1 == cost2) 0 else if (cost1 < cost2) 1 else -1
  }
}