package com.barrybecker4.discreteoptimization.common.graph.visualization

import com.barrybecker4.discreteoptimization.common.graph.directed.DirectedGraphParser
import com.barrybecker4.discreteoptimization.common.graph.visualization.GraphViewerFrame.{PARSER, PREFIX}
import com.barrybecker4.discreteoptimization.common.graph.visualization.{GraphStreamAdapter, GraphViewer}
import operations_research.pdlp.Solvers.AdaptiveLinesearchParamsOrBuilder
import org.graphstream.graph.Graph
import org.graphstream.ui.layout.springbox.implementations.{LinLog, SpringBox}
import org.graphstream.ui.swing_viewer.{SwingViewer, ViewPanel}
import org.graphstream.ui.view.{View, Viewer}

import java.io.File
import javax.swing.*
import scala.io.Source


object GraphViewerFrame {
  private val PREFIX = "scala-test/com/barrybecker4/discreteoptimization/shortestpaths/solver/data/"
  private val PARSER: DirectedGraphParser = DirectedGraphParser()
}

class GraphViewerFrame(inputGraph: Graph, title: String = "") extends JFrame("Graph Viewer") {
  System.setProperty("org.graphstream.ui", "swing")

  setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
  setSize(1000, 900)
  this.setTitle(title)

  var viewer: GraphViewer = _
  createMenu()

  setGraph(inputGraph, title)

  private def createMenu(): Unit = {
    val myMenuBar: JMenuBar = new JMenuBar()
    val fileMenu = new JMenu("File")
    val openItem = new JMenuItem("Open")
    fileMenu.add(openItem)
    myMenuBar.add(fileMenu)
    setJMenuBar(myMenuBar)
    openItem.addActionListener(_ => loadGraph())
  }

  private def setGraph(graph: Graph, title: String): Unit = {
    if (viewer != null)
      remove(viewer.getViewPanel)
    viewer = new GraphViewer(graph)
    this.setTitle(title)

    if (graph.getNode(0).hasAttribute("xy")) viewer.disableAutoLayout()
    else viewer.enableAutoLayout(SpringBox()) //SpringBox()) // LinLog()

    add(viewer.getViewPanel)
    this.repaint()
    setVisible(true)
  }

  private def loadGraph(): Unit = {
    val fileChooser = new JFileChooser()
    fileChooser.setCurrentDirectory(new File(PREFIX))

    val returnValue = fileChooser.showOpenDialog(GraphViewerFrame.this)
    if (returnValue == JFileChooser.APPROVE_OPTION) {
      val selectedFile = fileChooser.getSelectedFile
      // Now you can use the selected file to load the graph
      // Add here the logic to load the graph from the file
      println("Selected file: " + selectedFile.getAbsolutePath)
      // For example:
      // graph.read(selectedFile.getAbsolutePath)
      val source: Source = Source.fromFile(selectedFile.getAbsolutePath)
      val name = selectedFile.getName
      println("Name = " + name)
      val digraph = PARSER.parse(source, name)
      val graph = GraphStreamAdapter(digraph).createGraph()
      setGraph(graph, name)
    }
  }
}


