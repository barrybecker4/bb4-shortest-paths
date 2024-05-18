package com.barrybecker4.discreteoptimization.traffic.viewer.adapter

import com.barrybecker4.discreteoptimization.common.Location
import com.barrybecker4.discreteoptimization.common.graph.directed.DirectedGraph
import com.barrybecker4.discreteoptimization.common.graph.visualization.GraphStreamAdapter.LARGE_GRAPH_THRESH
import com.barrybecker4.discreteoptimization.common.graph.visualization.render.UiClass.*
import com.barrybecker4.discreteoptimization.traffic.graph.TrafficGraph
import com.barrybecker4.discreteoptimization.traffic.graph.model.{Intersection, Street}
import org.graphstream.graph.{Edge, Graph}
import org.graphstream.graph.implementations.MultiGraph
import org.graphstream.ui.geom.Point3
import com.barrybecker4.discreteoptimization.traffic.viewer.TrafficGraphUtil.{addEdgeLengths, showNodeLabels}

import scala.io.Source
import scala.util.Using


object TrafficStreamAdapter {
  val LARGE_GRAPH_THRESH = 60
  private val STYLE_SHEET_PATH =
    "scala-source/com/barrybecker4/discreteoptimization/traffic/viewer/adapter/traffic.css"

  private def loadStyleSheet(): String = {
    Using(Source.fromFile(STYLE_SHEET_PATH)) { source => source.mkString }
      .getOrElse(throw new RuntimeException(s"Failed to read the style sheet from $STYLE_SHEET_PATH"))
  }
}

/** Creates a stream graph from TrafficGraph
 */
case class TrafficStreamAdapter(trafficGraph: TrafficGraph) {

  private val isLarge = trafficGraph.intersections.size > LARGE_GRAPH_THRESH
  private var intersections: IndexedSeq[IntersectionSubGraph] = _

  def createGraph(): MultiGraph = {
    val graph = new MultiGraph("Some traffic graph")

    intersections = addIntersectionsToGraph(graph)
    addStreetsToGraph(graph)
    addEdgeLengths(graph)
    //showNodeLabels(graph)

    graph.setAttribute("ui.stylesheet", TrafficStreamAdapter.loadStyleSheet())
    graph.setAttribute("ui.antialias", true)
    graph
  }

  private def addIntersectionsToGraph(graph: MultiGraph): IndexedSeq[IntersectionSubGraph] = {
    for (intersectionId <- 0 until trafficGraph.numIntersections)
      yield IntersectionSubGraph(trafficGraph.getIntersection(intersectionId), graph)
  }

  // TODO refactor out street subGraph
  private def addStreetsToGraph(graph: MultiGraph): Unit = {
    var streetCount: Map[(Int, Int), Int] = Map()
    val uiClass = if (isLarge) LARGE.name else PLAIN.name
    for (street <- trafficGraph.streets) {

      val intersection1 = intersections(street.intersectionIdx1)
      val intersection2 = intersections(street.intersectionIdx2)
      val forwardEdge = graph.addEdge(getStreetEdgeId(street, true),
        intersection1.getOutgoingNode(street.portIdx1), intersection2.getIncomingNode(street.portIdx2), true)
      val backwardEdge = graph.addEdge(getStreetEdgeId(street, false),
        intersection2.getOutgoingNode(street.portIdx2), intersection1.getIncomingNode(street.portIdx1), true)

      forwardEdge.setAttribute("ui.class", uiClass)
      backwardEdge.setAttribute("ui.class", uiClass)

      addCurvePoints(forwardEdge, street)
      addCurvePoints(backwardEdge, street)
    }
  }

  // needs work
  private def addCurvePoints(edge: Edge, street: Street): Unit = {
    val src = edge.getSourceNode.getAttribute("xyz", classOf[Array[AnyRef]])
    val dst = edge.getTargetNode.getAttribute("xyz", classOf[Array[AnyRef]])

    val intersection1 = intersections(street.intersectionIdx1).intersection
    val intersection2 = intersections(street.intersectionIdx2).intersection

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