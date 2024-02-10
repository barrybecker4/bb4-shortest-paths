package com.barrybecker4.discreteoptimization.shortestpaths.viewer

import org.graphstream.graph.implementations.MultiGraph
import org.graphstream.ui.view.{ViewerListener, ViewerPipe}

case class GraphViewerListener(viewerPipe: ViewerPipe, graph: MultiGraph) extends ViewerListener  {
  
  override def viewClosed(viewName: String): Unit =
    println("closed "+ viewName)

  override def buttonPushed(id: String): Unit =
    println("button" + id + " pushed")

  override def buttonReleased(id: String): Unit =
    println("button" + id + " released")

  override def mouseOver(id: String): Unit =
    println("moused over " + id)

  override def mouseLeft(id: String): Unit =
    println("mouse left " + id)
}
