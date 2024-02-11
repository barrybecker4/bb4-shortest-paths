package com.barrybecker4.discreteoptimization.shortestpaths.viewer.render

import com.barrybecker4.discreteoptimization.common.graph.Path
import com.barrybecker4.discreteoptimization.shortestpaths.model.ShortestPathsSolution
import com.barrybecker4.discreteoptimization.shortestpaths.viewer.render.PathRenderer.{ANIMATION_DELAY, PAUSE}
import com.barrybecker4.discreteoptimization.shortestpaths.viewer.render.UiClass.*
import org.graphstream.graph.implementations.MultiGraph
import org.graphstream.graph.{Edge, Node}
import org.graphstream.ui.view.ViewerPipe

import java.awt.Color


object PathRenderer {
  private val ANIMATION_DELAY = 50
  private val PAUSE = 1000
}

case class PathRenderer(graph: MultiGraph, solution: ShortestPathsSolution, viewerPipe: ViewerPipe) {

  def render(): Unit = {
    val viewerListener = GraphViewerListener(viewerPipe, graph, this)
    viewerPipe.addViewerListener(viewerListener)

    //simulation and interaction happens in a separate path
    new Thread(() => {
//      Thread.sleep(PAUSE)
//      for (path <- solution.paths) {
//        colorPath(path, VISITED, ANIMATION_DELAY)
//      }
      listenForMouseEvents()
    }).start()
  }

  def colorPath(nodeIdx: Int, uiClass: UiClass): Unit = {
    val path = getPath(nodeIdx)
    colorPath(path, uiClass, 0)
  }

  def colorPath(path: Path, uiClass: UiClass, animationDelay: Int = ANIMATION_DELAY): Unit = {

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
    solution.paths.find(path => path.nodes.nonEmpty && path.lastNode == nodeIdx)
      .getOrElse(throw new IllegalStateException(s" count not find ${nodeIdx} among ${solution.paths.mkString("\n")}"))
  }

  private def listenForMouseEvents(): Unit = {
    while (true) {
      // use blockingPump to avoid 100% CPU usage
      viewerPipe.blockingPump();
    }
  }
}
