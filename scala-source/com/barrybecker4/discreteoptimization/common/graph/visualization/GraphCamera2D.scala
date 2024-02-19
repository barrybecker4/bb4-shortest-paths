package com.barrybecker4.discreteoptimization.common.graph.visualization

import org.graphstream.ui.geom.Point3
import org.graphstream.ui.graphicGraph.stylesheet.{Style, Values}
import org.graphstream.ui.graphicGraph.{GraphicEdge, GraphicElement, GraphicGraph, GraphicNode}
import org.graphstream.ui.view.camera.DefaultCamera2D

import java.awt.geom.Point2D

class GraphCamera2D(graphicGraph: GraphicGraph) extends DefaultCamera2D(graphicGraph) {

  /**
   * Override the edgeContains method to customize its behavior.
   * We would like to see if the x, y passed in are on the line connecting the nodes
   */
  override def edgeContains(elt: GraphicElement, x: Double, y: Double): Boolean = {

    val edge = elt.asInstanceOf[GraphicEdge]
    val node1 = edge.from
    val node2 = edge.to
    val pt1 = bck.transform(node1.getX, node1.getY, 0)
    val pt2 = bck.transform(node2.getX, node2.getY, 0)

    val distThresh = Math.max(2.0, elt.getStyle.getSize.size().toDouble)

    isPointNearLineSegment(pt1, pt2, Point3(x, y, 0), distThresh)
  }

  def isPointNearLineSegment(pt1: Point3, pt2: Point3, testPoint: Point3, distance: Double): Boolean = {
    val length = pt1.distance(pt2)
    val lengthSquared = length * length

    if (lengthSquared == 0.0) {
      // Points are the same, check if the test point coincides with them
      return pt1.distance(testPoint) <= distance
    }

    // Calculate the dot product of the vectors from pt1 to testPoint and pt1 to pt2
    val deltaX = pt2.x - pt1.x
    val deltaY = pt2.y - pt1.y
    val dotProduct = ((testPoint.x - pt1.x) * deltaX + (testPoint.y - pt1.y) * deltaY) / lengthSquared

    // Check if the projection falls within the segment
    if (dotProduct < 0.0 || dotProduct > 1.0) {
      return false
    }

    // Calculate the coordinates of the projection on the line
    val projectionX = pt1.x + dotProduct * (pt2.x - pt1.x)
    val projectionY = pt1.y + dotProduct * (pt2.y - pt1.y)

    // Check if the distance from the test point to the projection is within the specified threshold
    testPoint.distance(projectionX, projectionY, 0) <= distance
  }
}