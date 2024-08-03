package com.barrybecker4.discreteoptimization.pathviewer.render

import com.barrybecker4.graph.Path
import com.barrybecker4.graph.visualization.render.{CustomView, GraphViewerPipe, UiClass}
import com.barrybecker4.discreteoptimization.pathviewer.render.PathRenderer.{ANIMATION_DELAY, PAUSE}
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
}

trait PathRenderer(graph: MultiGraph, viewer: Viewer) {

  // The viewer pipe sends events from the UI thread to the render thread
  viewer.getDefaultView.setMouseManager(MouseOverMouseManager(util.EnumSet.of(InteractiveElement.EDGE, InteractiveElement.NODE)))

  protected val viewerPipe: ViewerPipe = GraphViewerPipe("my_custom_pipe", viewer.newViewerPipe())

  def render(): Unit = {
    val viewerListener = GraphViewerListener(viewerPipe, graph, this)
    viewerPipe.addViewerListener(viewerListener)

    // simulation and interaction happens in a separate thread
    new Thread(() => {
      initialAnimation()
      listenForMouseEvents()
    }).start()
  }

  protected def initialAnimation(): Unit = {}

  private def listenForMouseEvents(): Unit = {
    while (true) {
      // use blockingPump to avoid 100% CPU usage
      viewerPipe.blockingPump()
    }
  }
  
  /** color paths containing nodeIdx */
  def colorPaths(nodeIdx: Int, uiClass: UiClass): Unit

  /** color paths containing bith nodeIdx1 and nodeIdx2 */
  def colorPaths(nodeIdx1: Int, nodeIdx: Int, uiClass: UiClass): Unit

}
