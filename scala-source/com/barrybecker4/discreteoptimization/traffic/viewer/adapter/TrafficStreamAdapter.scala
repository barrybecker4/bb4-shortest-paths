package com.barrybecker4.discreteoptimization.traffic.viewer.adapter

import com.barrybecker4.discreteoptimization.common.Location
import com.barrybecker4.discreteoptimization.common.graph.directed.DirectedGraph
import com.barrybecker4.discreteoptimization.common.graph.visualization.GraphStreamAdapter.LARGE_GRAPH_THRESH
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
  private var intersectionSubGraphs: IndexedSeq[IntersectionSubGraph] = _

  def createGraph(): MultiGraph = {
    val graph = new MultiGraph("Some traffic graph")

    intersectionSubGraphs = addIntersectionsToGraph(graph)
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

  private def addStreetsToGraph(graph: MultiGraph): Unit = {
    var streetCount: Map[(Int, Int), Int] = Map()
    //val uiClass = if (isLarge) LARGE.name else PLAIN.name
    val streetSubGraphs: IndexedSeq[StreetSubGraph] =
      for (street <- trafficGraph.streets)
        yield StreetSubGraph(street, intersectionSubGraphs(street.intersectionIdx1), intersectionSubGraphs(street.intersectionIdx2), graph)

  }
}