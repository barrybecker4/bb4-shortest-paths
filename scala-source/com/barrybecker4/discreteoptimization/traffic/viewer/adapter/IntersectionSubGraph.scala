package com.barrybecker4.discreteoptimization.traffic.viewer.adapter

import com.barrybecker4.discreteoptimization.common.Location
import com.barrybecker4.discreteoptimization.traffic.graph.model.Intersection
import com.barrybecker4.discreteoptimization.traffic.signals.TrafficSignal
import com.barrybecker4.discreteoptimization.traffic.viewer.adapter.IntersectionSubGraph.{INTERSECTION_RADIUS, LANE_SEP_ANGLE, angleToRads}
import org.graphstream.graph.{Edge, Node}
import org.graphstream.graph.implementations.MultiGraph


object IntersectionSubGraph {
  val INTERSECTION_RADIUS = 110.0
  private val LANE_SEP_ANGLE = angleToRads(12)

  private def angleToRads(angle: Double): Double = angle.toFloat * Math.PI / 180.0
}

/**
 * Pupulates the graph with all the nodes and edges needed to represent the N-way intersection.
 * Provides convenient accessors for the streets that enter and exit the intersection.
 */
case class IntersectionSubGraph(intersection: Intersection, signal: TrafficSignal, graph: MultiGraph) {

  private val incomingNodes: Array[Node] = Array.ofDim[Node](intersection.ports.size)
  private val outgoingNodes: Array[Node] = Array.ofDim[Node](intersection.ports.size)

  populate()

  def getIncomingNode(portId: Int): Node = incomingNodes(portId)
  def getOutgoingNode(portId: Int): Node = outgoingNodes(portId)


  /**
   * For each port, create 2 nodes in a radial fashion around the center of the node.
   * The intersection nodes will then be connected up in the appropriate way.
   * Each incoming node can go to every outgoing node using a directed edge
   */
  private def populate(): Unit = {
    addPortNodes()
    addPortEdges()
  }
  
  private def addPortNodes(): Unit = {
    for (port <- intersection.ports) {
      val portId = port.id
      val inNode = graph.addNode(getNodeName(portId, "incoming"))
      val outNode = graph.addNode(getNodeName(portId, "outgoing"))
      val loc = intersection.location
      val ang = angleToRads(port.angle)

      inNode.setAttribute("xyz",
        loc.x + INTERSECTION_RADIUS * Math.cos(ang + LANE_SEP_ANGLE),
        loc.y + INTERSECTION_RADIUS * Math.sin(ang + LANE_SEP_ANGLE),
        0.0)
      outNode.setAttribute("xyz",
        loc.x + INTERSECTION_RADIUS * Math.cos(ang - LANE_SEP_ANGLE),
        loc.y + INTERSECTION_RADIUS * Math.sin(ang - LANE_SEP_ANGLE),
        0.0)

      incomingNodes(portId) = inNode
      outgoingNodes(portId) = outNode
    }
  }
  
  // For each incoming port, connect it to all the other outgoing port nodes
  private def addPortEdges(): Unit = {
    for (fromPortId <- intersection.ports.indices) {
      val fromNode: Node = incomingNodes(fromPortId)
      for (toPortId <- intersection.ports.indices) {
        if (toPortId != fromPortId) {
          val toNode = outgoingNodes(toPortId)
          val edge: Edge = graph.addEdge(getEdgeName(fromPortId, toPortId), fromNode, toNode, true)

          val src = fromNode.getAttribute("xyz", classOf[Array[AnyRef]])
          val dst = toNode.getAttribute("xyz", classOf[Array[AnyRef]])
          val srcVec = halfway(src, intersection.location)
          val dstVec = halfway(dst, intersection.location)
          edge.setAttribute("ui.points", 
            src(0), src(1), 0.0,
            srcVec(0), srcVec(1), 0,
            dstVec(0), dstVec(1), 0, 
            dst(0), dst(1), 0.0)
        }
      }
    }
  }
  
  private def halfway(pt1: Array[AnyRef], pt2: Location): Array[Double] = {
    Array((pt1(0).toString.toDouble + pt2.x) / 2.0, (pt1(1).toString.toDouble + pt2.y) / 2.0)
  }
  
  private def getNodeName(portId: Int, direction:String): String = 
    s"${intersection.id}_${portId}_$direction"
  
  private def getEdgeName(fromPortId: Int, toPortId: Int) = 
    s"${intersection.id}_from${fromPortId}_to${toPortId}"
}
