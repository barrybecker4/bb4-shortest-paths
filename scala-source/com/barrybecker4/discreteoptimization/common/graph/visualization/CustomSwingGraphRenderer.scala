package com.barrybecker4.discreteoptimization.common.graph.visualization

import org.graphstream.ui.graphicGraph.GraphicGraph
import org.graphstream.ui.swing.{BackendJ2D, SwingGraphRenderer}
import org.graphstream.ui.view.camera.DefaultCamera2D

import java.awt.Container


class CustomSwingGraphRenderer extends SwingGraphRenderer {

  override def open(graph: GraphicGraph, drawingSurface: Container): Unit = {
    if (this.graph == null) {
      this.graph = graph
      this.backend = new BackendJ2D

      // Use my custom camera
      this.camera = new GraphCamera2D(graph)
      graph.getStyleGroups.addListener(this)
      backend.open(drawingSurface)
    }
    else throw new RuntimeException("renderer already open, use close() first")
  }

}
