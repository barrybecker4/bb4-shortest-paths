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

  addCurvePoints(forwardEdge, street, forward = true)
  addCurvePoints(backwardEdge, street, forward = false)


  private def addCurvePoints(edge: Edge, street: Street, forward: Boolean): Unit = {
    val src = edge.getSourceNode.getAttribute("xyz", classOf[Array[AnyRef]])
    val dst = edge.getTargetNode.getAttribute("xyz", classOf[Array[AnyRef]])

    val intersection1 = if (forward) intersectionSubGraph1.intersection else intersectionSubGraph2.intersection
    val intersection2 = if (forward) intersectionSubGraph2.intersection else intersectionSubGraph1.intersection
    val portIdx1 = if (forward) street.portIdx1 else street.portIdx2
    val portIdx2 = if (forward) street.portIdx2 else street.portIdx1

    val srcCtrlPt = getPortSpokePoint(src, intersection1, portIdx1)
    val dstCtrlPt = getPortSpokePoint(dst, intersection2, portIdx2)

//    val halfwayPt = halfway(src, dst)
//    println(street.toString + " forward=" + forward)
//    println("halfwayPt=" + halfwayPt)
//    println("src=" + ptToString(src) + " srcVec=" + srcCtrlPt)
//    println("dst=" + ptToString(dst) + " dstVec=" + dstCtrlPt)

    edge.setAttribute("ui.points",
      src(0), src(1), 0.0,
      srcCtrlPt.x, srcCtrlPt.y, 0,
      dstCtrlPt.x, dstCtrlPt.y, 0,
      dst(0), dst(1), 0.0)
  }

  private def halfway(src: Array[AnyRef], dst: Array[AnyRef]): Location = {
    Location((src(0).toString.toFloat + dst(0).toString.toFloat) / 2.0f, (src(1).toString.toFloat + dst(1).toString.toFloat) / 2.0f)
  }

  private def getPortSpokePoint(pt: Array[AnyRef], intersection: Intersection, portId: Int): Location = {
    val radialPos = getRadialPosition(intersection, portId)
    Location(pt(0).toString.toFloat + radialPos.x, pt(1).toString.toFloat + radialPos.y)
  }

  private def getRadialPosition(intersection: Intersection, portId: Int): Location = {
    val port = intersection.ports(portId)
    //val len = IntersectionSubGraph.INTERSECTION_RADIUS + port.radialLength
    val vecX = (Math.cos(port.angleRad) * port.radialLength).toFloat
    val vecY = (Math.sin(port.angleRad) * port.radialLength).toFloat
    Location(vecX, vecY)
  }

  private def getStreetEdgeId(street: Street, isForward: Boolean): String = {
    if (isForward)
      s"${street.intersectionIdx1}_${street.portIdx1}-${street.intersectionIdx2}_${street.portIdx2}"
    else
      s"${street.intersectionIdx2}_${street.portIdx2}-${street.intersectionIdx1}_${street.portIdx1}"
  }

  private def ptToString(array: Array[AnyRef]): String =
    s"${array(0).toString}, ${array(1).toString}"
}
