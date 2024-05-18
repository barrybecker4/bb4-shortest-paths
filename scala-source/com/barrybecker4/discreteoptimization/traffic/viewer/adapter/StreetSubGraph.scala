package com.barrybecker4.discreteoptimization.traffic.viewer.adapter

import com.barrybecker4.discreteoptimization.common.Location
import com.barrybecker4.discreteoptimization.traffic.graph.model.{Intersection, Street}
import org.graphstream.graph.Edge
import org.graphstream.graph.implementations.MultiGraph
import com.barrybecker4.discreteoptimization.common.graph.visualization.render.UiClass.PLAIN


case class StreetSubGraph(street: Street,
                     intersectionSubGraph1: IntersectionSubGraph,
                     intersectionSubGraph2: IntersectionSubGraph,
                     graph: MultiGraph) {

  private val forwardEdge = graph.addEdge(getStreetEdgeId(street, true),
    intersectionSubGraph1.getOutgoingNode(street.portIdx1), intersectionSubGraph2.getIncomingNode(street.portIdx2), true)
  private val backwardEdge = graph.addEdge(getStreetEdgeId(street, false),
    intersectionSubGraph2.getOutgoingNode(street.portIdx2), intersectionSubGraph1.getIncomingNode(street.portIdx1), true)

  forwardEdge.setAttribute("ui.class", PLAIN.name)
  backwardEdge.setAttribute("ui.class", PLAIN.name)

  addCurvePoints(forwardEdge, street)
  addCurvePoints(backwardEdge, street)

  
  private def addCurvePoints(edge: Edge, street: Street): Unit = {
    val src = edge.getSourceNode.getAttribute("xyz", classOf[Array[AnyRef]])
    val dst = edge.getTargetNode.getAttribute("xyz", classOf[Array[AnyRef]])

    val intersection1 = intersectionSubGraph1.intersection
    val intersection2 = intersectionSubGraph2.intersection

    val srcVec = getPortSpokePoint(src, intersection1, street.portIdx1)
    val dstVec = getPortSpokePoint(dst, intersection2, street.portIdx2)

    edge.setAttribute("ui.points",
      src(0), src(1), 0.0,
      srcVec.x, srcVec.y, 0,
      dstVec.x, dstVec.y, 0,
      dst(0), dst(1), 0.0)
  }

  private def getPortSpokePoint(pt1: Array[AnyRef], intersection: Intersection, portId: Int): Location = {
    val radialPos = getRadialPosition(intersection, portId)
    Location(pt1(0).toString.toFloat + radialPos.x, pt1(1).toString.toFloat + radialPos.y)
  }

  private def getRadialPosition(intersection: Intersection, portId: Int): Location = {
    val port = intersection.ports(portId)
    //val len = IntersectionSubGraph.INTERSECTION_RADIUS + port.radialLength
    val vecX = (Math.cos(port.angle) * port.radialLength).toFloat
    val vecY = (Math.sin(port.angle) * port.radialLength).toFloat
    Location(vecX, vecY)
  }

  private def getStreetEdgeId(street: Street, isForward: Boolean): String = {
    if (isForward)
      s"${street.intersectionIdx1}_${street.portIdx1}-${street.intersectionIdx2}_${street.portIdx2}"
    else
      s"${street.intersectionIdx2}_${street.portIdx2}-${street.intersectionIdx1}_${street.portIdx1}"
  }
}
