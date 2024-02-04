package com.barrybecker4.discreteoptimization.shortestpaths

import com.barrybecker4.discreteoptimization.common.graph.Path
import com.barrybecker4.discreteoptimization.common.graph.directed.DirectedGraphParser
import com.barrybecker4.discreteoptimization.shortestpaths.GraphPathViewerFrame.{PARSER, PREFIX}
import com.barrybecker4.discreteoptimization.common.graph.visualization.{GraphStreamAdapter, GraphViewer, GraphViewerFrame}
import operations_research.pdlp.Solvers.AdaptiveLinesearchParamsOrBuilder
import org.graphstream.graph.{Graph, Node}
import org.graphstream.graph.implementations.MultiGraph
import org.graphstream.ui.layout.springbox.implementations.{LinLog, SpringBox}
import org.graphstream.ui.swing_viewer.{SwingViewer, ViewPanel}
import org.graphstream.ui.view.{View, Viewer, ViewerPipe}

import java.io.File
import javax.swing.*
import scala.io.Source


/**
 * Ideas:
 * Highlight the shortest path to a node upon mouseover
 * Implement similar for k shortest paths.
 */
object GraphPathViewerFrame {
  private val PREFIX = "scala-test/com/barrybecker4/discreteoptimization/shortestpaths/solver/data/"
  private val PARSER: DirectedGraphParser = DirectedGraphParser()
}

class GraphPathViewerFrame extends GraphViewerFrame() {

  override def createOpenItemOption(): JMenuItem = {
    val openItem = new JMenuItem("Open Shortest Paths")
    openItem.addActionListener(_ => loadShortestPaths())
    openItem
  }

  private def loadShortestPaths(): Unit = {
    val fileChooser = new JFileChooser()
    fileChooser.setCurrentDirectory(new File(PREFIX))


    val returnValue = fileChooser.showOpenDialog(GraphPathViewerFrame.this)
    if (returnValue == JFileChooser.APPROVE_OPTION) {
      val selectedFile = fileChooser.getSelectedFile
      println("selected file is " + selectedFile.getName)

      // first load the graph
      val graph = loadTheGraph(selectedFile)

      // then load the shortest paths
      loadShortestPaths(selectedFile, graph)
    }
  }

  private def loadTheGraph(file: File): MultiGraph = {
    val graphName = getGraphName(file.getName)
    val digraph = loadGraphFromName(graphName)
    val graph = GraphStreamAdapter(digraph).createGraph()
    setGraph(graph, file.getName)
    graph
  }

  private def loadShortestPaths(file: File, graph: MultiGraph): Unit = {
    val solution = ShortedPathsTstUtil.getSolution(file.getName)

    // Get the viewer pipe for sending events
    val viewerPipe: ViewerPipe = viewer.newViewerPipe()
    viewerPipe.addAttributeSink(graph)

    new Thread(() => {
      Thread.sleep(1000)
      for (path <- solution.paths) {
        explore(path, graph, viewerPipe)
      }
    }).start()
  }

  def explore(path: Path, graph: MultiGraph, viewerPipe: ViewerPipe): Unit = {

    if (path.nodes.size > 1) {
      var node = graph.getNode(path.nodes.head)
      var nextNode: Node = null //graph.getNode(path.nodes(1))

      for (nodeIdx <- path.nodes.tail) {
        val nextNode = graph.getNode(nodeIdx)
        val leavingEdge = node.leavingEdges().filter(e => e.getNode1 == nextNode).findFirst().get()
        nextNode.setAttribute("ui.class", "visited")
        leavingEdge.setAttribute("ui.class", "visited")
        // leavingEdge.setAttribute("ui.style", "size: 4;")
        node = nextNode
        viewerPipe.pump()
        Thread.sleep(100)
      }
    }
  }

  private def getGraphName(fileName: String): String = {
    val idx = fileName.indexOf("_modified_dijkstra")
    val start = if (idx > 0) idx else fileName.indexOf("_dijkstra_solution")
    fileName.substring(0, start)
  }

}


