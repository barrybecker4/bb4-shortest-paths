package com.barrybecker4.discreteoptimization.shortestpaths.viewer.render

import com.barrybecker4.discreteoptimization.common.graph.Path
import com.barrybecker4.discreteoptimization.shortestpaths.model.ShortestPathsSolution
import com.barrybecker4.discreteoptimization.shortestpaths.viewer.render.PathRenderer.{ANIMATION_DELAY, COLORS, PAUSE, colorToCss}
import org.graphstream.graph.implementations.MultiGraph
import org.graphstream.graph.{Edge, Node}
import org.graphstream.ui.view.ViewerPipe

import java.awt.Color

object PathRenderer {
  private val ANIMATION_DELAY = 50
  private val PAUSE = 1000
  private val COLORS: Array[Color] = Array(
    new Color(92, 205, 25),
    new Color(145, 215, 135),
    new Color(173, 204, 25),
    new Color(155, 195, 155),
    new Color(115, 155, 205),
    new Color(83, 165, 215),
    new Color(140, 117, 209),
    new Color(155, 97, 235),
    new Color(160, 104, 160),
    new Color(245, 137, 139),
  )

  private def colorToCss(color: Color): String =
    String.format("#%02x%02x%02x", color.getRed, color.getGreen, color.getBlue)
}

case class PathRenderer(graph: MultiGraph, solution: ShortestPathsSolution, viewerPipe: ViewerPipe) {

  def render(): Unit = {
    val viewerListener = GraphViewerListener(viewerPipe, graph, this)
    //viewerPipe.addAttributeSink(graph)
    viewerPipe.addViewerListener(viewerListener)

    //simulation and interaction happens in a separate path 
    new Thread(() => {
      Thread.sleep(PAUSE)
      for (path <- solution.paths) {
        colorPath(path, ANIMATION_DELAY)
      }
      listenForMouseEvents()
    }).start()
  }
  
  def colorPath(nodeIdx: Int): Unit = {
    val path = getPath(nodeIdx)
    println("coloring " + path)
    colorPath(path, 0)
  }
  
  def colorPath(path: Path, animationDelay: Int = ANIMATION_DELAY): Unit = {

    if (path.nodes.size > 1) {
      var prevNode: Node = null
      var nextNode: Node = null

      for (nodeIdx <- path.nodes) {
        val nextNode = graph.getNode(nodeIdx)
        val leavingEdge: Edge =
          if (prevNode != null) prevNode.leavingEdges().filter(e => e.getNode1 == nextNode).findFirst().get()
          else null
        nextNode.setAttribute("ui.class", "visited")
        val c = colorToCss(COLORS(nodeIdx % COLORS.length))
        nextNode.setAttribute("ui.style", s"fill-color: $c;");
        if (leavingEdge != null) {
          leavingEdge.setAttribute("ui.class", "visited")
          // leavingEdge.setAttribute("ui.style", "size: 4;")
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

  private def getPath(nodeIdx: Int): Path = {
    solution.paths.find(path => path.nodes.nonEmpty && path.nodes.last == nodeIdx)
      .getOrElse(throw new IllegalStateException(s" count not find ${nodeIdx} among ${solution.paths.mkString("\n")}"))
  }

  private def listenForMouseEvents(): Unit = {
    while (true) {
      // use blockingPump to avoid 100% CPU usage
      viewerPipe.blockingPump();
    }
  }
}
