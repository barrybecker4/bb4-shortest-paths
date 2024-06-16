package com.barrybecker4.discreteoptimization.traffic.viewer

import com.barrybecker4.discreteoptimization.common.graph.Path
import com.barrybecker4.discreteoptimization.common.graph.directed.{DirectedEdge, DirectedGraphParser}
import TrafficViewerFrame.{PARSER, SUFFIX, TRAFFIC_GRAPHS_PREFIX}
import com.barrybecker4.discreteoptimization.common.graph.visualization.render.GraphViewerPipe
import com.barrybecker4.discreteoptimization.common.graph.visualization.{GraphStreamAdapter, GraphViewer, GraphViewerFrame}
import com.barrybecker4.discreteoptimization.kshortestpaths.KShortedPathsTstUtil
import com.barrybecker4.discreteoptimization.kshortestpaths.model.KShortestPathsSolution
import com.barrybecker4.discreteoptimization.kshortestpaths.solver.YensKPathsSolver
import com.barrybecker4.discreteoptimization.pathviewer.PathViewerFrame.*
import com.barrybecker4.discreteoptimization.shortestpaths.ShortedPathsTstUtil
import com.barrybecker4.discreteoptimization.shortestpaths.model.ShortestPathsSolution
import com.barrybecker4.discreteoptimization.shortestpaths.solver.{DijkstrasPathSolver, ModifiedDijkstrasPathSolver}
import com.barrybecker4.discreteoptimization.traffic.demo.TrafficOrchestrator
import com.barrybecker4.discreteoptimization.traffic.viewer.adapter.{IntersectionSubGraph, TrafficStreamAdapter}
import com.barrybecker4.discreteoptimization.traffic.graph.{TrafficGraph, TrafficGraphParser}
import org.graphstream.graph.implementations.MultiGraph
import org.graphstream.graph.{Edge, Graph, Node}
import org.graphstream.ui.layout.springbox.implementations.{LinLog, SpringBox}
import org.graphstream.ui.swing_viewer.{SwingViewer, ViewPanel}
import org.graphstream.ui.view.{View, Viewer, ViewerListener, ViewerPipe}

import scala.concurrent.ExecutionContext.Implicits.global
import java.awt.Color
import java.io.File
import javax.swing.*
import scala.annotation.Annotation
import scala.concurrent.Future
import scala.io.Source


/** Draws the shortest paths and allows interacting with them.
 */
object TrafficViewerFrame {
  private val TRAFFIC_GRAPHS_PREFIX = "scala-test/com/barrybecker4/discreteoptimization/traffic/data/"
  private val PARSER: TrafficGraphParser = TrafficGraphParser()
  private val SUFFIX: String = ".txt"
}

class TrafficViewerFrame extends GraphViewerFrame() {

  override protected def createMenu(): Unit = {
    val myMenuBar: JMenuBar = new JMenuBar()
    val fileMenu = new JMenu("File")
    val openTrafficGraphItem = createOpenTrafficGraphItemOption()
    fileMenu.add(openTrafficGraphItem)
    myMenuBar.add(fileMenu)
    setJMenuBar(myMenuBar)
  }

  private def createOpenTrafficGraphItemOption(): JMenuItem = {
    val openItem = new JMenuItem("Open traffic map")
    openItem.addActionListener(_ => loadTrafficGraph())
    openItem
  }

  private def loadTrafficGraph(): Unit = {
    val fileChooser = new JFileChooser()
    fileChooser.setCurrentDirectory(new File(TRAFFIC_GRAPHS_PREFIX))

    val returnValue = fileChooser.showOpenDialog(TrafficViewerFrame.this)
    if (returnValue == JFileChooser.APPROVE_OPTION) {
      val selectedFile = fileChooser.getSelectedFile
      println("selected file is " + selectedFile.getName)

      val trafficGraph = loadTrafficGraph(selectedFile)
      val adapter = TrafficStreamAdapter(trafficGraph)
      val graph = adapter.createGraph()

      //val graph = TrafficGraphGenerator().generateGraph()
      //showTrafficGraph(graph)
      val initialSpeed = 1.0
      setGraph(graph, trafficGraph.numVehicles, initialSpeed, adapter.intersectionSubGraphs, "Traffic Demo")
    }
  }

  private def loadTrafficGraph(file: File): TrafficGraph = {
    val graphName = getGraphName(file.getName)
    loadTrafficGraphFromName(graphName)
  }

  def setGraph(graph: Graph, numVehicles: Int, 
               initialSpeed: Double, 
               intersectionSubGraphs: IndexedSeq[IntersectionSubGraph], 
               title: String): Unit = {

    if (viewer != null)
      remove(viewer.getViewPanel)
    viewer = new GraphViewer(graph)
    this.setTitle(title)

    // If the graph nodes have location data, don't use an layout
    if (graph.getNode(0).hasAttribute("xyz")) viewer.disableAutoLayout()
    else viewer.enableAutoLayout(SpringBox()) //SpringBox()) // LinLog()

    add(viewer.getViewPanel)
    this.repaint()
    setVisible(true)

    // must be run in a separate thread or it doesn't do anything
    val displayFuture: Future[Unit] = Future {
      new TrafficOrchestrator(graph, numVehicles, initialSpeed, intersectionSubGraphs, viewer.newViewerPipe()).run()
    }
    displayFuture.onComplete {
      case scala.util.Success(_) =>
        println("TrafficDemo completed successfully.")
      case scala.util.Failure(exception) =>
        println(s"TrafficDemo run failed with exception: $exception")
        val cause = exception.getCause
        cause.printStackTrace()
    }
  }

  private def getGraphName(fileName: String): String = {
    fileName.substring(0, fileName.length - SUFFIX.length)
  }

  private def loadTrafficGraphFromFile(file: File): TrafficGraph = {
    val name = file.getName
    val source: Source = Source.fromFile(file.getAbsolutePath)
    PARSER.parse(source, name)
  }

  private def loadTrafficGraphFromName(name: String): TrafficGraph = {
    loadTrafficGraphFromFile(new File(TRAFFIC_GRAPHS_PREFIX + name + SUFFIX))
  }
}
