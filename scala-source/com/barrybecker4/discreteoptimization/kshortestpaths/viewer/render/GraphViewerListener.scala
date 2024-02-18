package com.barrybecker4.discreteoptimization.kshortestpaths.viewer.render

import com.barrybecker4.discreteoptimization.kshortestpaths.viewer.render.PathRenderer
import com.barrybecker4.discreteoptimization.kshortestpaths.viewer.render.UiClass.*
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
    //println("moused over " + id)
    pathRenderer.colorPaths(id.toInt, HIGHLIGHTED)
  }

  override def mouseLeft(id: String): Unit =
    //println("moused out from " + id)
    pathRenderer.colorPaths(id.toInt, PLAIN)
}
