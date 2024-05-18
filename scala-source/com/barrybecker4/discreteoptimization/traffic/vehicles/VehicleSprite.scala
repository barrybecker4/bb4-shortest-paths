package com.barrybecker4.discreteoptimization.traffic.vehicles

import org.graphstream.graph.Edge
import org.graphstream.graph.Node
import org.graphstream.ui.spriteManager.Sprite
import org.graphstream.ui.spriteManager.SpriteManager


object VehicleSprite {
  // The is dependent on the size of the window and the coordinates used to build the graph
  private val SCALE = 10.0
}

class VehicleSprite(identifier: String, speed: Double, manager: SpriteManager) extends Sprite(identifier, manager) {
  private var step = 0.0

  override def attachToEdge(id: String): Unit = {
    super.attachToEdge(id)
    // add the sprite to a "sprites" attribute of the edge
  }

  override def detach(): Unit = {
    super.detach()
    // remove the sprite from the sprites attribute of the edge
  }
  
  def move(): Unit = {
    var p = getX
    if (step == 0) step = calculateIncrement(getAttachment.asInstanceOf[Edge])
    p += step
    if (p < 0 || p > 1) chooseNextEdge(p)
    else setPosition(p)
  }

  def chooseNextEdge(p: Double): Unit = {
    val edge = getAttachment.asInstanceOf[Edge]
    var node = edge.getSourceNode

    if (step > 0) node = edge.getTargetNode
    val nextEdge = randomEdge(node)
    val offset = Math.abs(p % 1)
    val pos = if (node eq nextEdge.getSourceNode) {
      step = calculateIncrement(nextEdge)
      offset
    } else {
      step = -calculateIncrement(nextEdge)
      1 - offset
    }
    attachToEdge(nextEdge.getId)
    setPosition(pos)
  }

  /** Move in larger percentage steps across shorter edges */
  private def calculateIncrement(edge: Edge): Double = {
    val edgeLen = edge.getAttribute("length", classOf[Object]).asInstanceOf[Double]
    speed * VehicleSprite.SCALE / edgeLen
  }

  /**
   * select an edge other than the one we came from
   */
  private def randomEdge(node: Node) = {
    val rand = (Math.random * node.getOutDegree).toInt
    node.getLeavingEdge(rand)
  }
}