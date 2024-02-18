package com.barrybecker4.discreteoptimization.pathviewer.render

import com.barrybecker4.discreteoptimization.common.graph.Path
import com.barrybecker4.discreteoptimization.shortestpaths.model.ShortestPathsSolution
import com.barrybecker4.discreteoptimization.pathviewer.render.PathRenderer.{ANIMATION_DELAY, PAUSE}
import com.barrybecker4.discreteoptimization.pathviewer.render.UiClass.*
import org.graphstream.graph.implementations.MultiGraph
import org.graphstream.graph.{Edge, Node}
import org.graphstream.ui.view.{Viewer, ViewerPipe}

import java.awt.Color


object ShortestPathRenderer {
  private val ANIMATION_DELAY = 20
  private val PAUSE = 100
}

case class ShortestPathRenderer(graph: MultiGraph, solution: ShortestPathsSolution, viewer: Viewer) extends PathRenderer(graph, viewer) {

  def render(): Unit = {
    val viewerListener = GraphViewerListener(viewerPipe, graph, this)
    viewerPipe.addViewerListener(viewerListener)

    // simulation and interaction happens in a separate thread
    new Thread(() => {
      listenForMouseEvents()
    }).start()
  }

  def colorPaths(nodeIdx: Int, uiClass: UiClass): Unit = {
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
    val optionalPath = solution.paths.find(path => path.nodes.nonEmpty && path.lastNode == nodeIdx)
    if (optionalPath.isEmpty) {
      println("There is no path to node " + nodeIdx)
      Path(Double.PositiveInfinity, List())
    }
    else optionalPath.get
  }

  private def listenForMouseEvents(): Unit = {
    while (true) {
      // use blockingPump to avoid 100% CPU usage
      viewerPipe.blockingPump();
    }
  }
}
