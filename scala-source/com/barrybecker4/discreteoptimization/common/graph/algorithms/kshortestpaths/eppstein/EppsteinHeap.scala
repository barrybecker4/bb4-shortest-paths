package com.barrybecker4.discreteoptimization.common.graph.algorithms.kshortestpaths.eppstein

import com.barrybecker4.discreteoptimization.common.graph.directed.DirectedEdge

import scala.collection.mutable.ArrayBuffer


/**
 * A pointer representation of an N-ary heap with data structures that aid in representing the heap constructed by
 * Eppstein's algorithm.
 * Does not contain functions for adding/removing elements while maintaining the heap property.
 * The children added to the root are sub-heaps whose elements are guaranteed to have a greater cost than the root
 * element of the heap.
 * TODO: try to make this immutable
 * @param sidetrack the sidetrack edge (u,v) associated with the root of this heap or sub-heap
 * @param sidetrackCost cost of the sidetrack
 * @param children supports N children but Eppstein is limited to 4
 * @param numOtherSidetracks number of elements of H_out(u) - 1
 */
class EppsteinHeap(var sidetrack: DirectedEdge, var sidetrackCost: Double, val children: ArrayBuffer[EppsteinHeap], var numOtherSidetracks: Int) {

  def this(sidetrack: DirectedEdge) =
    this(sidetrack, 0.0, new ArrayBuffer[EppsteinHeap](), 0)

  def this(sidetrack: DirectedEdge, sidetrackCost: Double) =
    this(sidetrack, sidetrackCost, new ArrayBuffer[EppsteinHeap](), 0)

  def addChild(child: EppsteinHeap): Unit =
    this.children.addOne(child)

  override def clone: EppsteinHeap = {
    val children_clone = new ArrayBuffer[EppsteinHeap](children.size)
    children_clone.addAll(children)
    new EppsteinHeap(sidetrack, sidetrackCost, children_clone, numOtherSidetracks)
  }
}