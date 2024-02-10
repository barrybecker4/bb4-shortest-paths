package com.barrybecker4.discreteoptimization.shortestpaths.viewer

import com.barrybecker4.discreteoptimization.common.graph.Path
import com.barrybecker4.discreteoptimization.shortestpaths.model.ShortestPathsSolution
import com.barrybecker4.discreteoptimization.shortestpaths.viewer.PathRenderer.{ANIMATION_DELAY, PAUSE, COLORS, colorToCss}
import org.graphstream.graph.{Edge, Node}
import org.graphstream.graph.implementations.MultiGraph
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

case class PathRenderer(graph: MultiGraph, viewerPipe: ViewerPipe) {

  def render(solution: ShortestPathsSolution): Unit = {
    val viewerListener = GraphViewerListener(viewerPipe, graph)
    //viewerPipe.addAttributeSink(graph)
    viewerPipe.addViewerListener(viewerListener)
    var ct = 0

    new Thread(() => {
      Thread.sleep(PAUSE)
      for (path <- solution.paths) {
        colorPath(path, graph, viewerPipe, ct)
        ct += 1
      }
      listenForMouseEvents(viewerPipe)
    }).start()
  }


  def colorPath(path: Path, graph: MultiGraph, viewerPipe: ViewerPipe, pathNum: Int): Unit = {

    if (path.nodes.size > 1) {
      var prevNode: Node = null
      var nextNode: Node = null

      for (nodeIdx <- path.nodes) {
        val nextNode = graph.getNode(nodeIdx)
        val leavingEdge: Edge =
          if (prevNode != null) prevNode.leavingEdges().filter(e => e.getNode1 == nextNode).findFirst().get()
          else null
        nextNode.setAttribute("ui.class", "visited")
        val c = colorToCss(COLORS(pathNum % COLORS.length))
        nextNode.setAttribute("ui.style", s"fill-color: ${c};");
        if (leavingEdge != null) {
          leavingEdge.setAttribute("ui.class", "visited")
          // leavingEdge.setAttribute("ui.style", "size: 4;")
        }
        prevNode = nextNode
        viewerPipe.pump()
        Thread.sleep(ANIMATION_DELAY)
      }
    }
  }

  private def listenForMouseEvents(viewerPipe: ViewerPipe): Unit = {
    while (true) {
      // use blockingPump to avoid 100% CPU usage
      viewerPipe.blockingPump();
    }
  }
}
