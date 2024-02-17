package com.barrybecker4.discreteoptimization.common.graph.visualization

import org.graphstream.graph.Graph
import org.graphstream.ui.layout.springbox.implementations.{LinLog, SpringBox}
import org.graphstream.ui.swing_viewer.{SwingViewer, ViewPanel}
import org.graphstream.ui.view.{View, Viewer}

import javax.swing.{JFrame, JLabel, WindowConstants}

/**
 * Renders a graph in another thread (separate from UI thread) to avoid janky performance.
 * @param graph
 */
class GraphViewer(graph: Graph) extends SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD) {

  def getViewPanel: ViewPanel =
    addDefaultView(false).asInstanceOf[ViewPanel]
  
}


