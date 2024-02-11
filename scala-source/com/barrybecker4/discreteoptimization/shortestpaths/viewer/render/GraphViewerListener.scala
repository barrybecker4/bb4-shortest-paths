package com.barrybecker4.discreteoptimization.shortestpaths.viewer.render

import com.barrybecker4.discreteoptimization.shortestpaths.viewer.render.PathRenderer
import com.barrybecker4.discreteoptimization.shortestpaths.viewer.render.UiClass.*
import org.graphstream.graph.implementations.MultiGraph
import org.graphstream.ui.view.{ViewerListener, ViewerPipe}

case class GraphViewerListener(viewerPipe: ViewerPipe, graph: MultiGraph, pathRenderer: PathRenderer) extends ViewerListener  {
  
  override def viewClosed(viewName: String): Unit =
    println("closed "+ viewName)

  override def buttonPushed(id: String): Unit =
    println("button" + id + " pushed")

  override def buttonReleased(id: String): Unit =
    println("button" + id + " released")

  override def mouseOver(id: String): Unit = {
    pathRenderer.colorPath(id.toInt, HIGHLIGHTED)
  }

  override def mouseLeft(id: String): Unit =
    pathRenderer.colorPath(id.toInt, PLAIN)
}
