package com.barrybecker4.discreteoptimization.kshortestpaths.viewer.render

import org.graphstream.graph.implementations.MultiGraph
import org.graphstream.ui.swing_viewer.util.MouseOverMouseManager
import org.graphstream.ui.view.util.InteractiveElement
import org.graphstream.ui.view.{View, ViewerPipe}

import java.awt.event.MouseEvent
import java.util


class GraphMouseManager(view: View, pathRenderer: PathRenderer) extends MouseOverMouseManager(util.EnumSet.of(InteractiveElement.EDGE), 0) {
  
  override def mouseEntered(event: MouseEvent): Unit = {
    println("looking (enter)")
    val edge = view.findGraphicElementAt(util.EnumSet.of(InteractiveElement.EDGE), event.getX, event.getY)

    if (edge != null) {
      System.out.println("Mouse entered edge: " + edge.getId)
    }
  }

  override def mouseExited(event: MouseEvent): Unit = {
    val edge = view.findGraphicElementAt(util.EnumSet.of(InteractiveElement.EDGE), event.getX, event.getY)
    if (edge != null) {
      System.out.println("Mouse left edge: " + edge.getId)
    }
  }
}
