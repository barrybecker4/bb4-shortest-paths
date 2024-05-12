package com.barrybecker4.discreteoptimization.common.graph.traffic.viewer.adapter

import com.barrybecker4.discreteoptimization.common.Location
import com.barrybecker4.discreteoptimization.common.graph.directed.DirectedGraph
import com.barrybecker4.discreteoptimization.common.graph.traffic.TrafficGraph
import com.barrybecker4.discreteoptimization.common.graph.visualization.GraphStreamAdapter.LARGE_GRAPH_THRESH
import com.barrybecker4.discreteoptimization.common.graph.visualization.render.UiClass.*
import org.graphstream.graph.implementations.MultiGraph
import org.graphstream.ui.geom.Point3

import scala.io.Source
import scala.util.Using


object TrafficStreamAdapter {
  val LARGE_GRAPH_THRESH = 300
  private val STYLE_SHEET_PATH =
    "scala-source/com/barrybecker4/discreteoptimization/common/graph/traffic/viewer/adapter/traffic.css"

  private def loadStyleSheet(): String = {
    Using(Source.fromFile(STYLE_SHEET_PATH)) { source => source.mkString }
      .getOrElse(throw new RuntimeException(s"Failed to read the style sheet from $STYLE_SHEET_PATH"))
  }
}

/**
 * Creates a stream graph from TrafficGraph
 */
case class TrafficStreamAdapter(trafficGraph: TrafficGraph) {

  private val isLarge = trafficGraph.intersections.size > LARGE_GRAPH_THRESH

  def createGraph(): MultiGraph = {
    val graph = new MultiGraph("Some traffic graph")

    addIntersectionsToGraph(graph)
    addStreetsToGraph(graph)
    graph.setAttribute("ui.stylesheet", TrafficStreamAdapter.loadStyleSheet())
    graph.setAttribute("ui.antialias", true)
    graph
  }

  private def addIntersectionsToGraph(graph: MultiGraph): Unit = {
    for (intersectionId <- Range(0, trafficGraph.numIntersections)) {
      val node = graph.addNode(intersectionId.toString)

      node.setAttribute("ui.label", node.getId)

      val location: Location = trafficGraph.getLocation(intersectionId)
      node.setAttribute("xy", location.x, location.y)
    }
  }

  private def addStreetsToGraph(graph: MultiGraph): Unit = {
    var streetCount: Map[(Int, Int), Int] = Map()
    val uiClass = if (isLarge) LARGE.name else PLAIN.name
    for (street <- trafficGraph.streets) {
      val edgeTuple = (street.intersectionIdx1, street.intersectionIdx2)
      val reverseTuple = (street.intersectionIdx2, street.intersectionIdx1)
      val baseId = s"${street.intersectionIdx1}-${street.intersectionIdx2}"
      val streetId =
        if (streetCount.contains(edgeTuple)) baseId + "_" + streetCount(edgeTuple)
        else baseId
      val graphEdge = graph.addEdge(streetId, street.intersectionIdx1.toString, street.intersectionIdx2.toString, true)
      streetCount += edgeTuple -> (streetCount.getOrElse(edgeTuple, 0) + 1)
      graphEdge.setAttribute("ui.class", uiClass)
      graphEdge.setAttribute("ui.style", "shape: cubic-curve;")
      graphEdge.setAttribute("ui.curve", new Point3(7, 1, 0), new Point3(3, 3, 0))
    }
  }
}