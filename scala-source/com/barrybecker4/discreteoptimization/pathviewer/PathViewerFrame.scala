package com.barrybecker4.discreteoptimization.pathviewer

import com.barrybecker4.graph.Path
import com.barrybecker4.graph.directed.{DirectedEdge, DirectedGraphParser}
import com.barrybecker4.graph.visualization.{GraphStreamAdapter, GraphViewer, GraphViewerFrame}
import com.barrybecker4.discreteoptimization.kshortestpaths.KShortedPathsTstUtil
import com.barrybecker4.discreteoptimization.kshortestpaths.model.KShortestPathsSolution
import com.barrybecker4.discreteoptimization.kshortestpaths.solver.YensKPathsSolver
import com.barrybecker4.discreteoptimization.pathviewer.PathViewerFrame.{K_SHORTEST_PATHS_PREFIX, SHORTEST_PATHS_PREFIX}
import com.barrybecker4.discreteoptimization.pathviewer.render.ShortestPathRenderer
import com.barrybecker4.discreteoptimization.pathviewer.render.KShortestPathRenderer
import com.barrybecker4.discreteoptimization.shortestpaths.ShortedPathsTstUtil
import com.barrybecker4.discreteoptimization.shortestpaths.model.ShortestPathsSolution
import com.barrybecker4.discreteoptimization.pathviewer.PathViewerFrame.*
import com.barrybecker4.discreteoptimization.pathviewer.render.{GraphViewerListener, PathRenderer}
import com.barrybecker4.discreteoptimization.shortestpaths.solver.{DijkstrasPathSolver, ModifiedDijkstrasPathSolver}
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
object PathViewerFrame {
  private val SHORTEST_PATHS_PREFIX = "scala-test/com/barrybecker4/discreteoptimization/shortestpaths/solver/data/"
  private val K_SHORTEST_PATHS_PREFIX = "scala-test/com/barrybecker4/discreteoptimization/kshortestpaths/solver/data/"
  private val PARSER: DirectedGraphParser = DirectedGraphParser()
}

class PathViewerFrame extends GraphViewerFrame() {

  override protected def createMenu(): Unit = {
    val myMenuBar: JMenuBar = new JMenuBar()
    val fileMenu = new JMenu("File")
    val openShortestPathsItem = createOpenShortestPathsItemOption()
    val openKShortestPathsItem = createOpenKShortestPathsItemOption()
    fileMenu.add(openShortestPathsItem)
    fileMenu.add(openKShortestPathsItem)
    myMenuBar.add(fileMenu)
    setJMenuBar(myMenuBar)
  }
  private def createOpenShortestPathsItemOption(): JMenuItem = {
    val openItem = new JMenuItem("Open Shortest Paths")
    openItem.addActionListener(_ => loadShortestPaths())
    openItem
  }

  private def createOpenKShortestPathsItemOption(): JMenuItem = {
    val openItem = new JMenuItem("Open K Shortest Paths")
    openItem.addActionListener(_ => loadKShortestPaths())
    openItem
  }

  private def loadShortestPaths(): Unit = {
    val fileChooser = new JFileChooser()
    fileChooser.setCurrentDirectory(new File(SHORTEST_PATHS_PREFIX))

    val returnValue = fileChooser.showOpenDialog(PathViewerFrame.this)
    if (returnValue == JFileChooser.APPROVE_OPTION) {
      val selectedFile = fileChooser.getSelectedFile
      println("selected file is " + selectedFile.getName)

      val graph = loadTheGraph(selectedFile)
      val solution = ShortedPathsTstUtil.getSolution(selectedFile.getName)

      // then load the shortest paths
      showShortestPaths(solution, graph)
    }
  }

  private def loadKShortestPaths(): Unit = {
    val fileChooser = new JFileChooser()
    fileChooser.setCurrentDirectory(new File(K_SHORTEST_PATHS_PREFIX))

    val returnValue = fileChooser.showOpenDialog(PathViewerFrame.this)
    if (returnValue == JFileChooser.APPROVE_OPTION) {
      val selectedFile = fileChooser.getSelectedFile
      println("selected file is " + selectedFile.getName)

      val graph = loadTheGraph(selectedFile)
      val solution = KShortedPathsTstUtil.getSolution(selectedFile.getName)

      // then load the shortest paths
      showKShortestPaths(solution, graph)
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

    ShortestPathRenderer(graph, solution, viewer).render()
  }

  private def showKShortestPaths(solution: KShortestPathsSolution, graph: MultiGraph): Unit = {

    KShortestPathRenderer(graph, solution, viewer).render()
  }

  private def getGraphName(fileName: String): String = {
    val start = getStartIndex(fileName)
    fileName.substring(0, start)
  }

  private def getStartIndex(fileName: String): Int = {
    var idx =  fileName.indexOf(s"_${ModifiedDijkstrasPathSolver.BASE_NAME}_solution")
    if (idx == -1)
      idx = fileName.indexOf(s"_${DijkstrasPathSolver.BASE_NAME}_solution")
    if (idx == -1)
      idx = fileName.indexOf(s"_${YensKPathsSolver.BASE_NAME}_solution")
    if (idx == -1)
      throw new IllegalArgumentException("Invalid fileName: " + fileName)
    idx
  }
}
