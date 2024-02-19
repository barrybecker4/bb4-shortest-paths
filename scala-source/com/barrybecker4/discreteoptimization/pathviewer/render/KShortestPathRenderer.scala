package com.barrybecker4.discreteoptimization.pathviewer.render

import com.barrybecker4.discreteoptimization.common.graph.Path
import com.barrybecker4.discreteoptimization.common.graph.visualization.GraphViewer
import com.barrybecker4.discreteoptimization.kshortestpaths.model.KShortestPathsSolution
import com.barrybecker4.discreteoptimization.pathviewer.render.PathRenderer.{ANIMATION_DELAY, PAUSE, colorToCss}
import com.barrybecker4.discreteoptimization.pathviewer.render.KShortestPathRenderer.COLORS
import com.barrybecker4.discreteoptimization.pathviewer.render.UiClass.{PLAIN, VISITED}
import org.graphstream.graph.implementations.MultiGraph
import org.graphstream.graph.{Edge, Node}
import org.graphstream.ui.view.ViewerPipe

import java.awt.Color


object KShortestPathRenderer {
  private val COLORS: Array[Color] = Array(
    new Color(165, 105, 85),
    new Color(90, 160, 30),
    new Color(70, 110, 180),
    new Color(130, 50, 160),
    new Color(135, 25, 25),
    new Color(170, 110, 10),
    new Color(135, 165, 45),
    new Color(100, 100, 220),
    new Color(115, 175, 135),
    new Color(90, 130, 150),
    new Color(5, 155, 105),
  )

}

case class KShortestPathRenderer(graph: MultiGraph, solution: KShortestPathsSolution, viewer: GraphViewer) extends PathRenderer(graph, viewer) {
  
  def render(): Unit = {
    val viewerListener = GraphViewerListener(viewerPipe, graph, this)
    viewerPipe.addViewerListener(viewerListener)
    

    // simulation and interaction happens in a separate thread
    new Thread(() => {
      var ct = 0
      for (path <- solution.shortestPaths) {
        colorPath(path, VISITED, ANIMATION_DELAY, Some(COLORS(ct)))
        colorPath(path, VISITED, ANIMATION_DELAY, None)
        ct += 1
      }
      listenForMouseEvents()
    }).start()
  }

  private def listenForMouseEvents(): Unit = {
    while (true) {
      // use blockingPump to avoid 100% CPU usage
      viewerPipe.blockingPump();
    }
  }

  def colorPaths(nodeIdx: Int, uiClass: UiClass): Unit = {
    val pathIndices = getPathIndices(nodeIdx)
    colorPaths(pathIndices, uiClass)
  }

  def colorPaths(nodeIdx1: Int, nodeIdx2: Int, uiClass: UiClass): Unit = {
    val pathIndices = getPathIndices(nodeIdx1, nodeIdx2)
    colorPaths(pathIndices, uiClass)
  }
  
  private def colorPaths(pathIndices: Seq[Int], uiClass: UiClass): Unit = {
    if (pathIndices.nonEmpty) {
      var ct = pathIndices.head
      val paths = solution.shortestPaths.slice(ct, ct + pathIndices.length)
      for (path <- paths) {
        if (uiClass == PLAIN) colorPath(path, PLAIN, 0)
        else colorPath(path, uiClass, 0, Some(COLORS(ct)))
        ct += 1
      }
    }
  }

  def colorPath(path: Path, uiClass: UiClass, animationDelay: Int = ANIMATION_DELAY, color: Option[Color] = None): Unit = {

    if (path.nodes.size > 1) {
      var prevNode: Node = null
      var nextNode: Node = null
      val pathIdx = path.lastNode

      for (nodeIdx <- path.nodes) {
        val nextNode = graph.getNode(nodeIdx)
        val leavingEdge: Edge =
          if (prevNode != null) prevNode.leavingEdges().filter(e => e.getNode1 == nextNode).findFirst().get()
          else null
        nextNode.setAttribute("ui.class", uiClass.name)

        if (leavingEdge != null) {
          leavingEdge.setAttribute("ui.class", uiClass.name)
          if (color.isDefined) {
            val c = colorToCss(color.get)
            leavingEdge.setAttribute("ui.style", s"fill-color: $c; size: 3;")
          } else {
            leavingEdge.setAttribute("ui.style", "size: 0;")
          }
        }
        prevNode = nextNode
        if (animationDelay > 0) {
          viewerPipe.pump()
          Thread.sleep(animationDelay)
        }
      }
      if (animationDelay == 0) viewerPipe.pump()
    }
  }

  private def getPathIndices(nodeIdx: Int): Seq[Int] =
    solution.shortestPaths.zipWithIndex.filter((path, idx) => path.nodes.contains(nodeIdx)).map(_._2)


  private def getPathIndices(nodeIdx1: Int, nodeIdx2: Int): Seq[Int] =
    solution.shortestPaths.zipWithIndex.filter((path, idx) => path.nodes.contains(nodeIdx1) && path.nodes.contains(nodeIdx2)).map(_._2)  

}
