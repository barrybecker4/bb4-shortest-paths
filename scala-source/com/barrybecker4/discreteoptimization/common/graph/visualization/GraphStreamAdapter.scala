package com.barrybecker4.discreteoptimization.common.graph.visualization

import com.barrybecker4.discreteoptimization.common.Location
import com.barrybecker4.discreteoptimization.common.graph.directed.DirectedGraph
import org.graphstream.graph.implementations.MultiGraph

import scala.io.Source
import scala.util.Using


object GraphStreamAdapter {
  private val STYLE_SHEET_PATH = "scala-source/com/barrybecker4/discreteoptimization/common/graph/visualization/graph.css"

  private def loadStyleSheet(): String = {
    Using(Source.fromFile(STYLE_SHEET_PATH)) { source => source.mkString }
      .getOrElse(throw new RuntimeException(s"Failed to read the style sheet from $STYLE_SHEET_PATH"))
  }
}

/**
 * Creates a stream graph from DirectedGraph
 */
case class GraphStreamAdapter(digraph: DirectedGraph) {

  def createGraph(): MultiGraph = {
    val graph = new MultiGraph("Some Graph")

    addNodesToGraph(graph)
    addEdgesToGraph(graph)
    graph.setAttribute("ui.stylesheet", GraphStreamAdapter.loadStyleSheet())
    graph.setAttribute("ui.antialias", true)
    graph
  }

  private def addNodesToGraph(graph: MultiGraph): Unit = {
    for (nodeId <- Range(0, digraph.numVertices)) {
      val node = graph.addNode(nodeId.toString)
      node.setAttribute("ui.label", node.getId)
      if (digraph.locations.isDefined) {
        val location: Location = digraph.locations.get(nodeId)
        node.setAttribute("xy", location.x, location.y)
      }
    }
  }

  private def addEdgesToGraph(graph: MultiGraph): Unit = {

    var edgeSet: Set[(Int, Int)] = Set()
    for (edge <- digraph.edges) {
      val edgeId = s"${edge.source}-${edge.destination}"
      val graphEdge = graph.addEdge(edgeId, edge.source.toString, edge.destination.toString, true)
      edgeSet += (edge.source, edge.destination)
      val weightText =
        if (edgeSet.contains((edge.destination, edge.source))) s"          ${edge.weight}"
        else s"${edge.weight}           "
      graphEdge.setAttribute("ui.label", weightText)
    }
  }
}