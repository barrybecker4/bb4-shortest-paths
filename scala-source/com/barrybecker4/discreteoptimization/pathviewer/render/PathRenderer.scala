package com.barrybecker4.discreteoptimization.pathviewer.render

import com.barrybecker4.discreteoptimization.common.graph.Path
import com.barrybecker4.discreteoptimization.common.graph.visualization.CustomView
import com.barrybecker4.discreteoptimization.pathviewer.render.PathRenderer.{ANIMATION_DELAY, PAUSE}
import com.barrybecker4.discreteoptimization.pathviewer.render.UiClass
import com.barrybecker4.discreteoptimization.pathviewer.render.GraphViewerPipe
import org.graphstream.graph.implementations.MultiGraph
import org.graphstream.graph.{Edge, Node}
import org.graphstream.ui.graphicGraph.GraphicGraph
import org.graphstream.ui.swing_viewer.util.MouseOverMouseManager
import org.graphstream.ui.view.util.InteractiveElement
import org.graphstream.ui.view.{Viewer, ViewerPipe}

import java.awt.Color
import java.util
import java.util.EnumSet


object PathRenderer {
  val ANIMATION_DELAY = 20
  val PAUSE = 100

  def colorToCss(color: Color): String =
    String.format("#%02x%02x%02x", color.getRed, color.getGreen, color.getBlue)
}

trait PathRenderer(graph: MultiGraph, viewer: Viewer) {

  // The viewer pipe sends events from the UI thread to the render thread
  viewer.getDefaultView.setMouseManager(MouseOverMouseManager(util.EnumSet.of(InteractiveElement.EDGE, InteractiveElement.NODE)))

  protected val viewerPipe: ViewerPipe = GraphViewerPipe("my_custom_pipe", viewer.newViewerPipe())

  def render(): Unit

  def colorPaths(nodeIdx: Int, uiClass: UiClass): Unit

}
