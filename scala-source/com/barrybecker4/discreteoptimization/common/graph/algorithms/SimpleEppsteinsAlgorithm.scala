package com.barrybecker4.discreteoptimization.common.graph.algorithms

import com.barrybecker4.discreteoptimization.common.BoundedPriorityQueue
import com.barrybecker4.discreteoptimization.common.graph.Path
import com.barrybecker4.discreteoptimization.common.graph.Path.EMPTY_PATH
import com.barrybecker4.discreteoptimization.common.graph.directed.{ChangeableDirectedGraph, DirectedEdge, DirectedGraph}

import scala.annotation.tailrec
import scala.collection.mutable


/**
 * Modified from Brandon Smock's work at https://github.com/bsmock/k-shortest-paths/blob/master/edu/ufl/cise/bsmock/graph/ksp/SimpleEppstein.java
 *
 * This is "simplified Eppstein's algorithm", a version of Eppstein's algorithm for computing the K shortest paths
 * between two nodes in a graph, which has been simplified by Brandon Smock.
 *
 * This simplified version eliminates the pre-processing and additional storage required by Eppstein's
 * algorithm. It is faster for smaller values of K due to the reduction in pre-processing, but it incurs more
 * computation per path, so for large values of K, Eppstein's algorithm is faster.
 *
 * It is primarily intended to be a useful first step for understanding how Eppstein's algorithm works and how it can be
 * implemented.
 */
class SimpleEppsteinsAlgorithm(graph: DirectedGraph) extends KShortestPathsFinder {

  def isLoopless: Boolean = false

  /**
   * Computes the K shortest paths (allowing cycles) in a graph from source to destination using a simple
   * version of Eppstein's algorithm.
   * @param source starting node for all of the paths
   * @param target ending node for all of the paths
   * @param K      number of shortest paths to compute
   */
  def findKShortestPaths(source: Int, target: Int, K: Int): List[Path] = {
    kspCutoff(source, target, K, Double.MaxValue)
  }

  /**
   * Computes the K shortest paths (allowing cycles) in a graph from node s to node t in graph G using a simplified
   * version of Eppstein's algorithm. ("Finding the k Shortest Paths", Eppstein)
   *
   * See Eppstein.java for some explanatory notes about how Eppstein's algorithm works.
   *
   * - In this simplified version, like Eppstein's algorithm we represent each path using its unique sequence of
   * sidetrack edges.
   * - Like Eppstein's algorithm, there is a min heap which partially orders these paths, and we can use this fact
   * to efficiently search the space of paths to find the K shortest.
   * - Like Eppstein's algorithm, we never fully generate this heap, and we can use a second heap to store the
   * portions of the first heap that we have traversed/generated.
   *
   * DIFFERENCES IN THIS SIMPLIFIED VERSION:
   * - The first heap has O(|E|) children per node, and one of the big contributions of Eppstein was a scheme for
   * re-organizing this heap to have at most 4 children per node
   * - This requires some not insignificant computation at the beginning of the procedure, but then requires much less
   * computation per path, since the search for paths expands at a much smaller rate, which is independent of the size
   * of the graph.
   * - In this simplified version, we do not re-organize the path heap, nor keep track of children in any way.
   * - This saves space and eliminates a lot of pre-processing computation but does require more computation per path.
   * - Thus for small to moderate values of K, this simplified algorithm ought to be faster than Eppstein's algorithm,
   * but for large values of K, Eppstein's algorithm will eventually overtake this algorithm in efficiency.
   * @param source starting node for all of the paths
   * @param target ending node for all of the paths
   * @param K      number of shortest paths to compute
   * @param threshold maximum cost allowed for a path
   * @return a list of the K shortest paths from s to t, ordered from shortest to longest
   */
  def kspCutoff(source: Int, target: Int, K: Int, threshold: Double): List[Path] = {
    // Compute the shortest path tree, T, for the target node (the shortest path from every node in the graph to the target)
    val tree: ShortestPaths = new DijkstrasAlgorithm(graph).findShortestPaths(source)

    // Compute the set of sidetrack edge costs
    val sidetrackEdgeCostMap = computeSidetrackEdgeCosts(tree)

    // Initialize the containers for the candidate k shortest paths and the actual found k shortest paths
    var ksp: List[Path] = List()
    val pathPQ = mutable.PriorityQueue[ImplicitPath]()

    // Place the shortest path in the candidate-path priority queue
    pathPQ.addOne(ImplicitPath(DirectedEdge(-1, source, 0), graph, -1, tree.distToVertex(source)))

    /* Pop k times from the candidate-path priority queue to determine the k shortest paths */
    var k = 0
    while (k < K && pathPQ.size > 0) {
      // Get the next shortest path, which is implicitly represented as:
      //        1) A parent, shorter path, p, from s (source) to t (target)
      //        2) A sidetrack edge which branches off of path p at node u, and points to node v
      //        3) The shortest path (in the shortest path tree) from node v to t
      val kpathImplicit = pathPQ.dequeue()

      // Convert from the implicit path representation to the explicit path representation
      val kpath = kpathImplicit.explicitPath(ksp, tree)
      // Optional/added step: Stop if this path is above the cost/length threshold (if a threshold exists)
      if (kpath.weight > threshold) return ksp
      // Add explicit path to the list of K shortest paths
      ksp +:= kpath

      // Push the O(|E|) children of this path within the path heap onto the priority queue as new candidates
      addChildrenToQueue(sidetrackEdgeCostMap, kpathImplicit, k, pathPQ)
      k += 1
    }

    // Return the set of k shortest paths
    ksp
  }

  /**
   * Compute the set of sidetrack edge costs.
   *
   * Each sidetrack edge (u,v) is an edge in graph G that does not appear in the shortest path tree, T.
   * For every sidetrack edge (u,v), compute S(u,v) = w(u,v) + d(v) - d(u), where w(u,v) is the cost of edge (u,v);
   * and d(v) is the cost of the shortest path from node v to the target.
   * @param tree  the shortest path tree, T, rooted at the target node, t
   */
  protected def computeSidetrackEdgeCosts(tree: ShortestPaths): Map[String, Double] = {
    var sidetrackEdgeCostMap: Map[String, Double] = Map()
    val edgeList = graph.edges

    for (edge <- edgeList) {
      // Check to see if the target node is reachable from the outgoing vertex of the current edge,
      // and check to see if the current edge is a sidetrack edge. If so, calculate its sidetrack cost.
      val tp = tree.previousNode(edge.source)
      if (tp.isEmpty || !(tp.get == edge.destination)) {
        val sidetrackEdgeCost = edge.weight + tree.distToVertex(edge.destination) - tree.distToVertex(edge.source)
        sidetrackEdgeCostMap += (edge.source + "," + edge.destination) -> sidetrackEdgeCost
      }
    }
    sidetrackEdgeCostMap
  }

  /**
   * Push the children of the given (kth) path, onto the priority queue.
   * @param sidetrackMap  map container with all of the costs associated with sidetrack edges
   * @param kpathImplicit implicit representation of the (kth) path
   * @param kpathImplicit implicit representation of the previous/parent (kth) shortest path
   * @param k             k, the index of the previous/parent shortest path
   * @param pathPQ        priority queue of candidate paths
   */
  protected def addChildrenToQueue(sidetrackMap: Map[String, Double], kpathImplicit: ImplicitPath, k: Int,
                                   pathPQ: mutable.PriorityQueue[ImplicitPath]): Unit = {
    val kpathCost: Double = kpathImplicit.cost
    // Each path is represented as a sequence of sidetrack edges.
    // Each path's children are all of the paths with the same sequence of sidetrack edges followed by one additional sidetrack.
    // Therefore, starting from the last sidetrack edge of the parent (kth) path, its children correspond to each
    //  sidetrack edge reachable by traversing non-sidetrack edges only in the graph.
    //  These can be found using a depth-first search.

    // Initialize the stack for the DFS
    var edgeStack: List[DirectedEdge] = List()
    // Add the neighbors of the last sidetrack edge in the graph to the stack for DFS
    for (outgoingEdge <- graph.outgoingNeighborsOf(kpathImplicit.sidetrackEdge.destination)) {
      edgeStack +:= outgoingEdge
    }
    // Iterate/execute the DFS
    while (edgeStack.nonEmpty) {
      val poppedEdge = edgeStack.head
      edgeStack = edgeStack.tail
      val edgeString = poppedEdge.source + "," + poppedEdge.destination
      if (sidetrackMap.contains(edgeString)) {
        // Base case for DFS: sidetrack edge found
        // Add the child/candidate path represented by the sidetrack edge to the priority queue
        val candidate = new ImplicitPath(poppedEdge, graph, k, kpathCost + sidetrackMap(edgeString))
        pathPQ.addOne(candidate)
      }
      else {
        // Recursive case for DFS: sidetrack edge not reached, keep going (if current node has outgoing edges)
        for (outgoingEdge <- graph.outgoingNeighborsOf(poppedEdge.destination))
          edgeStack +:= outgoingEdge
      }
    }
  }
}

