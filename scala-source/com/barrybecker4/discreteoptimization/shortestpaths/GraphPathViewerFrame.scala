package com.barrybecker4.discreteoptimization.shortestpaths

import com.barrybecker4.discreteoptimization.common.graph.directed.DirectedGraphParser
import com.barrybecker4.discreteoptimization.shortestpaths.GraphPathViewerFrame.{PARSER, PREFIX}
import com.barrybecker4.discreteoptimization.common.graph.visualization.{GraphStreamAdapter, GraphViewer, GraphViewerFrame}
import operations_research.pdlp.Solvers.AdaptiveLinesearchParamsOrBuilder
import org.graphstream.graph.Graph
import org.graphstream.ui.layout.springbox.implementations.{LinLog, SpringBox}
import org.graphstream.ui.swing_viewer.{SwingViewer, ViewPanel}
import org.graphstream.ui.view.{View, Viewer}

import java.io.File
import javax.swing.*
import scala.io.Source


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

    // first load the graph
    val returnValue = fileChooser.showOpenDialog(GraphPathViewerFrame.this)
    if (returnValue == JFileChooser.APPROVE_OPTION) {
      val selectedFile = fileChooser.getSelectedFile
      println("selected file is " + selectedFile.getName)

      val graphName = getGraphName(selectedFile.getName)
      val digraph = loadGraphFromName(graphName)
      val graph = GraphStreamAdapter(digraph).createGraph()
      setGraph(graph, selectedFile.getName)
    }

    // then load the shortest paths

  }

  private def getGraphName(fileName: String): String = {
    val idx = fileName.indexOf("_modified_dijkstra")
    val start = if (idx > 0) idx else fileName.indexOf("_dijkstra_solution")
    fileName.substring(0, start)
  }

}


