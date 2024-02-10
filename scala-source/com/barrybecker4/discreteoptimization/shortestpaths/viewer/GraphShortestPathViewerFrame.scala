package com.barrybecker4.discreteoptimization.shortestpaths.viewer

import com.barrybecker4.discreteoptimization.common.graph.Path
import com.barrybecker4.discreteoptimization.common.graph.directed.{DirectedEdge, DirectedGraphParser}
import com.barrybecker4.discreteoptimization.common.graph.visualization.{GraphStreamAdapter, GraphViewer, GraphViewerFrame}
import com.barrybecker4.discreteoptimization.shortestpaths.ShortedPathsTstUtil
import com.barrybecker4.discreteoptimization.shortestpaths.viewer.{GraphShortestPathViewerFrame, GraphViewerListener}
import com.barrybecker4.discreteoptimization.shortestpaths.viewer.GraphShortestPathViewerFrame.*
import operations_research.pdlp.Solvers.AdaptiveLinesearchParamsOrBuilder
import org.graphstream.graph.implementations.MultiGraph
import org.graphstream.graph.{Edge, Graph, Node}
import org.graphstream.ui.layout.springbox.implementations.{LinLog, SpringBox}
import org.graphstream.ui.swing_viewer.{SwingViewer, ViewPanel}
import org.graphstream.ui.view.{View, Viewer, ViewerListener, ViewerPipe}

import java.awt.Color
import java.io.File
import javax.swing.*
import scala.annotation.Annotation
import scala.io.Source


/**
 * Ideas:
 * Highlight the shortest path to a node upon mouseover
 * Implement similar for k shortest paths.
 */
object GraphShortestPathViewerFrame {
  private val PREFIX = "scala-test/com/barrybecker4/discreteoptimization/shortestpaths/solver/data/"
  private val PARSER: DirectedGraphParser = DirectedGraphParser()
  private val ANIMATION_DELAY = 50
  private val PAUSE = 1000
  private val COLORS: Array[Color] = Array(
    new Color(92, 205, 25),
    new Color(145, 215, 135),
    new Color(173, 204, 25),
    new Color(155, 195,155),
    new Color(115, 155, 205),
    new Color(83, 165, 215),
    new Color(140, 117, 209),
    new Color(155, 97, 235),
    new Color(160, 104, 160),
    new Color(245, 137, 139),
  )

  private def colorToCss(color: Color): String =
    String.format("#%02x%02x%02x", color.getRed, color.getGreen, color.getBlue)
}

class GraphShortestPathViewerFrame extends GraphViewerFrame() {

  override def createOpenItemOption(): JMenuItem = {
    val openItem = new JMenuItem("Open Shortest Paths")
    openItem.addActionListener(_ => loadShortestPaths())
    openItem
  }

  private def loadShortestPaths(): Unit = {
    val fileChooser = new JFileChooser()
    fileChooser.setCurrentDirectory(new File(PREFIX))

    val returnValue = fileChooser.showOpenDialog(GraphShortestPathViewerFrame.this)
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
    val viewerListener = GraphViewerListener(viewerPipe, graph)
    viewer.getDefaultView.enableMouseOptions()

    //viewerPipe.addAttributeSink(graph)
    viewerPipe.addViewerListener(viewerListener)
    var ct = 0

    new Thread(() => {
      Thread.sleep(PAUSE)
      for (path <- solution.paths) {
        explore(path, graph, viewerPipe, ct)
        ct += 1
      }
      listenForMouseEvents(viewerPipe)
    }).start()

  }

  def explore(path: Path, graph: MultiGraph, viewerPipe: ViewerPipe, pathNum: Int): Unit = {

    if (path.nodes.size > 1) {
      var prevNode: Node = null
      var nextNode: Node = null

      for (nodeIdx <- path.nodes) {
        val nextNode = graph.getNode(nodeIdx)
        val leavingEdge: Edge =
          if (prevNode != null) prevNode.leavingEdges().filter(e => e.getNode1 == nextNode).findFirst().get()
          else null
        nextNode.setAttribute("ui.class", "visited")
        val c = colorToCss(COLORS(pathNum % COLORS.length))
        nextNode.setAttribute("ui.style", s"fill-color: ${c};");
        if (leavingEdge != null) {
          leavingEdge.setAttribute("ui.class", "visited")
          // leavingEdge.setAttribute("ui.style", "size: 4;")
        }
        prevNode = nextNode
        viewerPipe.pump()
        Thread.sleep(ANIMATION_DELAY)
      }
    }
  }

  private def listenForMouseEvents(viewerPipe: ViewerPipe): Unit = {
    while (true) {
      // use blockingPump to avoid 100% CPU usage
      viewerPipe.blockingPump();
    }
  }

  private def getGraphName(fileName: String): String = {
    val idx = fileName.indexOf("_modified_dijkstra")
    val start = if (idx > 0) idx else fileName.indexOf("_dijkstra_solution")
    fileName.substring(0, start)
  }

}
