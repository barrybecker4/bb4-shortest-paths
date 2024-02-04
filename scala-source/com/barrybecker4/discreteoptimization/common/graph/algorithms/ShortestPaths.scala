package com.barrybecker4.discreteoptimization.common.graph.algorithms

import com.barrybecker4.discreteoptimization.common.graph.directed.DirectedEdge
import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer


/**
 * @param numNodes the number of nodes in the graph.
 * @param source the node that we will traverse from.
 */
case class ShortestPaths(numNodes: Int, source: Int) {

  private val edgeTo = ArrayBuffer.fill[Option[DirectedEdge]](numNodes)(None)
  private val distTo = ArrayBuffer.fill(numNodes)(Double.PositiveInfinity)
  distTo(source) = 0.0
  
  def pathNodesToVertex(vertex: Int): Seq[Int] = {
    val path = pathToVertex(vertex)
    if (path.isEmpty) Seq.empty
    else {
      val start: Int = path.head.source
      val nodes = path.map(_.destination)
      start +: nodes
    }
  }
  
  def isBetterEdge(edge: DirectedEdge): Boolean = 
    distTo(edge.source) + edge.weight < distTo(edge.destination)
  
  def useEdge(edge: DirectedEdge): Unit = {
    distTo(edge.destination) = distTo(edge.source) + edge.weight
    edgeTo(edge.destination) = Some(edge)
  }

  def distToVertex(v: Int): Double = distTo(v)

  /** @return sequence of edges which form the path from source vertex to vertex
   */
  private def pathToVertex(vertex: Int): Seq[DirectedEdge] = {

    @tailrec
    def go(list: List[DirectedEdge], v: Int): List[DirectedEdge] =
      edgeTo(v) match {
        case Some(e) => go(e +: list, e.source)
        case None => list
      }

    if (hasPath(vertex)) go(List(), vertex) else Seq()
  }

  private def hasPath(v: Int): Boolean = edgeTo(v).isDefined
}