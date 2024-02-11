package com.barrybecker4.discreteoptimization.shortestpaths.viewer

import com.barrybecker4.discreteoptimization.common.graph.Path
import com.barrybecker4.discreteoptimization.common.graph.directed.{DirectedEdge, DirectedGraphParser}
import com.barrybecker4.discreteoptimization.common.graph.visualization.{GraphStreamAdapter, GraphViewer, GraphViewerFrame}
import com.barrybecker4.discreteoptimization.shortestpaths.ShortedPathsTstUtil
import com.barrybecker4.discreteoptimization.shortestpaths.model.ShortestPathsSolution
import com.barrybecker4.discreteoptimization.shortestpaths.viewer.GraphShortestPathViewerFrame
import com.barrybecker4.discreteoptimization.shortestpaths.viewer.GraphShortestPathViewerFrame.*
import com.barrybecker4.discreteoptimization.shortestpaths.viewer.render.{GraphViewerListener, PathRenderer}
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


/** Draws the shortest paths and allows interacting with them.
 */
object GraphShortestPathViewerFrame {
  private val PREFIX = "scala-test/com/barrybecker4/discreteoptimization/shortestpaths/solver/data/"
  private val PARSER: DirectedGraphParser = DirectedGraphParser()
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

      val graph = loadTheGraph(selectedFile)
      val solution = ShortedPathsTstUtil.getSolution(selectedFile.getName)

      // then load the shortest paths
      showShortestPaths(solution, graph)
    }
  }

  private def loadTheGraph(file: File): MultiGraph = {
    val graphName = getGraphName(file.getName)
    val digraph = loadGraphFromName(graphName)
    val graph = GraphStreamAdapter(digraph).createGraph()
    setGraph(graph, file.getName)
    graph
  }

  private def showShortestPaths(solution: ShortestPathsSolution, graph: MultiGraph): Unit = {

    // Get the viewer pipe for sending events
    val viewerPipe: ViewerPipe = viewer.newViewerPipe()
    viewer.getDefaultView.enableMouseOptions()
    
    PathRenderer(graph, solution, viewerPipe).render()
  }

  private def getGraphName(fileName: String): String = {
    val idx = fileName.indexOf("_modified_dijkstra")
    val start = if (idx > 0) idx else fileName.indexOf("_dijkstra_solution")
    fileName.substring(0, start)
  }
}
