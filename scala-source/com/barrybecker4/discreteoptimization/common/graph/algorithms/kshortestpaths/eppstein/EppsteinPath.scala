package com.barrybecker4.discreteoptimization.common.graph.algorithms.kshortestpaths.eppstein

import com.barrybecker4.discreteoptimization.common.graph.Path
import com.barrybecker4.discreteoptimization.common.graph.algorithms.kshortestpaths.eppstein.EppsteinHeap
import com.barrybecker4.discreteoptimization.common.graph.directed.{DirectedGraph, DirectedEdge}

/**
 * Data structure for representing a source-target path implicitly inside the priority queue of candidate k shortest
 * paths during the execution of Eppstein's algorithm.
 */
class EppsteinPath(var heap: EppsteinHeap, // pointer to the heap node and last sidetrack edge in this candidate path
                   var prefPath: Int,   // index of the shorter path that this path sidetracks from
                   var cost: Double     // the total cost of the path
   ) extends Comparable[EppsteinPath] {


  /**
   * Convert from the implicit representation of the path to an explicit listing of all of the edges in the path
   * There are potentially three pieces to the path:
   * 1) the path from node s (source) to node u in the parent path
   * 2) the sidetrack edge (u,v)
   * 3) the shortest path (in the shortest path tree) from node v to node t (target)
   */
  def explicitPath(ksp: List[Path], tree: ShortestPathTree, graph: DirectedGraph): Path = {
    var explicitPath = Path.EMPTY_PATH
    var totalWeight: Double = 0
    var pathNodes: List[Int] = List()

    // If path is not the shortest path in the graph...
    if (prefPath >= 0) {
      // Get the explicit representation of the shorter parent path that this path sidetracks from
      val explicitPrefPath = ksp(prefPath)
      // 1a) Identify the s-u portion of the path
      // Identify and add the segment of the parent path up until the point where the current path sidetracks off
      // of it.
      // In other words, if (u,v) is the sidetrack edge of the current path off of the parent path, look for the
      // last instance of node u in the parent path.
      val nodes: Array[Int] = explicitPrefPath.nodes.toArray
      var lastNodeNum = -1
      val heapSidetrack = heap.sidetrack
      var i = nodes.size - 1
      var done = false
      while (i > 0 && !done) {
        val currentNode = nodes(i)
        if (currentNode.equals(heapSidetrack.source)) {
          lastNodeNum = i
          done = true
        }
      }
      // 1b) Add the s-u portion of the path
      // Copy the explicit parent path up to the identified point where the current/child path sidetracks
      pathNodes +:= nodes.head
      var prevNode = nodes.head
      for (i <- 1 to lastNodeNum) {
        val node = nodes(i)
        pathNodes +:= node
        totalWeight += graph.findEdge(prevNode, node).weight
        prevNode = node
      }
      // 2) Add the (u,v) portion of the path
      // Add the last sidetrack edge to the explicit path representation
      pathNodes +:= (heap.sidetrack.destination)
      totalWeight += heap.sidetrackCost
    }
    // 3) Add the v-t portion of the path
    // Add the shortest path from v (either the source node, or the incoming node of the sidetrack edge associated
    // with the current path) to the explicit path representation
    var current = heap.sidetrack.destination
    while (!(current == tree.root)) {
      val next = tree.getParentOf(current)
      val edgeWeight = tree.nodes(current).dist - tree.nodes(next).dist
      pathNodes +:= next
      totalWeight += edgeWeight
      current = next
    }
    Path(totalWeight, pathNodes.reverse)
  }

  override def compareTo(comparedNode: EppsteinPath): Int = {
    val cost1 = this.cost
    val cost2 = comparedNode.cost
    if (cost1 == cost2) return 0
    if (cost1 > cost2) return 1
    else -1
  }
}