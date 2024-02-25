package com.barrybecker4.discreteoptimization.pathviewer.render

import com.barrybecker4.discreteoptimization.common.graph.Path
import com.barrybecker4.discreteoptimization.common.graph.visualization.GraphViewer
import com.barrybecker4.discreteoptimization.common.graph.visualization.render.UiClass
import com.barrybecker4.discreteoptimization.kshortestpaths.model.KShortestPathsSolution
import com.barrybecker4.discreteoptimization.pathviewer.render.PathRenderer.{ANIMATION_DELAY, PAUSE}
import com.barrybecker4.discreteoptimization.pathviewer.render.PathColors.*
import com.barrybecker4.discreteoptimization.common.graph.visualization.render.UiClass.*
import org.graphstream.graph.implementations.MultiGraph
import org.graphstream.graph.{Edge, Node}
import org.graphstream.ui.view.ViewerPipe

import java.awt.Color


case class KShortestPathRenderer(graph: MultiGraph, solution: KShortestPathsSolution, viewer: GraphViewer)
  extends PathRenderer(graph, viewer) {

  override protected def initialAnimation(): Unit = {
    var ct = 0
    for (path <- solution.shortestPaths) {
      colorPath(path, VISITED, ANIMATION_DELAY, Some(getColor(ct)))
      colorPath(path, VISITED, ANIMATION_DELAY, None)
      ct += 1
    }
  }

  override def colorPaths(nodeIdx: Int, uiClass: UiClass): Unit = {
    val pathIndices = getPathIndices(nodeIdx)
    colorPaths(pathIndices, uiClass)
  }

  override def colorPaths(nodeIdx1: Int, nodeIdx2: Int, uiClass: UiClass): Unit = {
    val pathIndices = getPathIndices(nodeIdx1, nodeIdx2)
    colorPaths(pathIndices, uiClass)
  }
  
  private def colorPaths(pathIndices: Seq[Int], uiClass: UiClass): Unit = {
    if (pathIndices.nonEmpty) {
      var ct = pathIndices.head
      val paths = solution.shortestPaths.slice(ct, ct + pathIndices.length)
      for (path <- paths) {
        if (uiClass == PLAIN || uiClass == LARGE) colorPath(path, uiClass, 0)
        else colorPath(path, uiClass, 0, Some(getColor(ct)))
        ct += 1
      }
    }
  }

  def colorPath(path: Path, uiClass: UiClass, animationDelay: Int = ANIMATION_DELAY, color: Option[Color] = None): Unit = {

    if (path.nodes.size > 1) {
      var prevNode: Node = null
      var nextNode: Node = null
      val lastNodeIdx = path.lastNode

      for (nodeIdx <- path.nodes) {
        val nextNode = graph.getNode(nodeIdx)
        val leavingEdge: Edge =
          if (prevNode != null) prevNode.leavingEdges().filter(e => e.getNode1 == nextNode).findFirst().get()
          else null
        if (nodeIdx == lastNodeIdx && uiClass.isHighlight)
          nextNode.setAttribute("ui.class", "last")
        else
          nextNode.setAttribute("ui.class", uiClass.name)

        if (leavingEdge != null) {
          leavingEdge.setAttribute("ui.class", uiClass.name)
          if (color.isDefined) {
            val c = colorToCss(color.get)
            leavingEdge.setAttribute("ui.style", s"fill-color: $c; size: 3;")
          } else {
            leavingEdge.setAttribute("ui.style", "size: 2;")
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

  // Get all the paths that pass through nodeIdx
  private def getPathIndices(nodeIdx: Int): Seq[Int] =
    solution.shortestPaths.zipWithIndex.filter((path, idx) => path.containsNode(nodeIdx)).map(_._2)


  private def getPathIndices(nodeIdx1: Int, nodeIdx2: Int): Seq[Int] = {
    solution.shortestPaths.zipWithIndex.filter((path, idx) => {
      val nodes = path.nodes
      val containsBoth = path.containsNode(nodeIdx1) && path.containsNode(nodeIdx2)
      containsBoth && (Math.abs(nodes.indexOf(nodeIdx2) - nodes.indexOf(nodeIdx1)) == 1)
    }).map(_._2)
  }

}
