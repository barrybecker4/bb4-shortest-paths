package com.barrybecker4.discreteoptimization.common.graph.visualization

import com.barrybecker4.discreteoptimization.common.Location
import com.barrybecker4.discreteoptimization.common.graph.directed.DirectedGraph
import com.barrybecker4.discreteoptimization.common.graph.visualization.GraphStreamAdapter.STYLE_SHEET
import org.graphstream.graph.implementations.MultiGraph


object GraphStreamAdapter {
  private val STYLE_SHEET =
    """
      |graph {
      |  fill-color: #eeffee;
      |}
      |node {
      |  shape: circle;
      |  size: 20px, 20px;
      |  fill-color: #ffffff;
      |  fill-mode: plain;
      |  stroke-mode: plain;
      |  stroke-color: #000088;
      |  text-mode: normal;
      |  text-alignment: center;
      |  text-size: 12px;
      |  text-color: #000077;
      |}
      |node.visited {
      |  size: 23px, 23px;
      |  stroke-color: #880066;
      |  text-size: 14px;
      |  text-color: #990066;
      |}
      |edge {
      |  text-size: 15px;
      |  text-mode: normal;
      |  text-alignment: center;
      |  text-background-mode: none;
      |  text-color: #4466aa;
      |  text-alignment: under;
      |}
      |edge.visited {
      |  text-size: 16px;
      |  text-color: #aa0000;
      |  stroke-color: #aa0066;
      |  size: 2;
      |}
      |""".stripMargin
}

/**
 * Creates a stream graph from DirectedGraph
 */
case class GraphStreamAdapter(digraph: DirectedGraph) {

  def createGraph(): MultiGraph = {
    val graph = new MultiGraph("Some Graph")

    addNodesToGraph(graph)
    addEdgesToGraph(graph)

    graph.setAttribute("ui.stylesheet", STYLE_SHEET)
    graph
  }

  private def addNodesToGraph(graph: MultiGraph): Unit = {
    for (nodeId <- Range(0, digraph.numVertices)) {
      val node = graph.addNode(nodeId.toString)
      node.setAttribute("ui.label", node.getId)
      if (digraph.locations.isDefined)
        val location: Location = digraph.locations.get(nodeId)
        node.setAttribute("xy", location.x, location.y)
    }
  }

  private def addEdgesToGraph(graph: MultiGraph): Unit = {
    var edgeId = 0
    var edgeSet: Set[(Int, Int)] = Set()
    for (edge <- digraph.edges) {
      val graphEdge = graph.addEdge(edgeId.toString, edge.source.toString, edge.destination.toString, true)
      edgeSet += (edge.source, edge.destination)
      val weightText = if (edgeSet.contains((edge.destination, edge.source))) s"          ${edge.weight}" else s"${edge.weight}           "
      graphEdge.setAttribute("ui.label", weightText)
      edgeId += 1
    }
  }
}
