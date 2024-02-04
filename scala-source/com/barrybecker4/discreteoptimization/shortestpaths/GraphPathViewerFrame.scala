package com.barrybecker4.discreteoptimization.shortestpaths

import com.barrybecker4.discreteoptimization.common.graph.directed.DirectedGraphParser
import com.barrybecker4.discreteoptimization.common.graph.visualization.GraphViewerFrame.{PARSER, PREFIX}
import com.barrybecker4.discreteoptimization.common.graph.visualization.{GraphStreamAdapter, GraphViewer, GraphViewerFrame}
import operations_research.pdlp.Solvers.AdaptiveLinesearchParamsOrBuilder
import org.graphstream.graph.Graph
import org.graphstream.ui.layout.springbox.implementations.{LinLog, SpringBox}
import org.graphstream.ui.swing_viewer.{SwingViewer, ViewPanel}
import org.graphstream.ui.view.{View, Viewer}

import java.io.File
import javax.swing.*
import scala.io.Source



class GraphPathViewerFrame(inputGraph: Graph, title: String = "Graph Path Viewer") extends GraphViewerFrame(inputGraph, title) {

  override def createMenu(): Unit = {
    val myMenuBar: JMenuBar = new JMenuBar()
    val fileMenu = new JMenu("File")
    val openItem = new JMenuItem("Open Shortest Paths")
    fileMenu.add(openItem)
    myMenuBar.add(fileMenu)
    setJMenuBar(myMenuBar)
    openItem.addActionListener(_ => loadGraph())
  }

}


