package com.barrybecker4.discreteoptimization.traffic.vehicles

import com.barrybecker4.discreteoptimization.traffic.vehicles.VehicleSprite.{MAX_SPEED, SCALE}
import org.graphstream.graph.Edge
import org.graphstream.graph.Node
import org.graphstream.ui.spriteManager.Sprite
import com.barrybecker4.discreteoptimization.traffic.vehicles.VehicleSpriteManager


object VehicleSprite {
  // The is dependent on the size of the window and the coordinates used to build the graph
  private val SCALE = 1.0
  // Meters/second
  private val MAX_SPEED = 100
}

/**
 * @param identifier name of the sprite
 * @param initialSpeed initial speed of the sprite in meters per second
 */
class VehicleSprite(identifier: String, initialSpeed: Double, manager: VehicleSpriteManager) extends Sprite(identifier, manager) {
  private var positionPct: Double = 0.0
  private var step = 0.0
  private var speed = initialSpeed

  def getSpeed: Double = speed

  /** @param percent a number between -100 and 100. The caller should be sure that the acceleration is not too great
   */
  def changeSpeed(percent: Double): Unit = {
    speed *= (1.0 + percent)
    if (speed < 0) {
      println("error: speed was less than 0 - " + speed)
      speed = 0
    } else if (speed > MAX_SPEED) {
      println("error: speed was > MAX - " + speed)
      speed = MAX_SPEED
    }
    println("new speed = " + speed)
  }

  override def attachToEdge(id: String): Unit = {
    val edge = manager.getEdge(id)
    if (this.attachment != edge ) {
      this.detach()
      this.attachment = edge
    }
    this.attachment.setAttribute(this.completeId, Array(0.0: java.lang.Double))

    manager.addVehicleToEdge(id, this)
  }

  override def detach(): Unit = {
    if (attachment != null) {
      manager.removeVehicleFromEdge(attachment.getId, this)
      super.detach()
    }
  }
  
  def move(deltaTime: Double): Unit = {
    var p = getX
    if (step == 0) step = calculateIncrement(getAttachment.asInstanceOf[Edge], deltaTime)
    p += step
    if (p < 0 || p > 1) chooseNextEdge(p, deltaTime)
    else setPosition(p)
  }

  def chooseNextEdge(p: Double, deltaTime: Double): Unit = {
    val edge = getAttachment.asInstanceOf[Edge]
    var node = edge.getSourceNode

    if (step > 0) node = edge.getTargetNode
    val nextEdge = randomEdge(node)
    val offset: Double = Math.abs(p % 1)
    val pos = if (node eq nextEdge.getSourceNode) {
      step = calculateIncrement(nextEdge, deltaTime)
      offset
    } else {
      // For the traffic sim, we never do this, because the vehicles always move forward.
      step = -calculateIncrement(nextEdge, deltaTime)
      1 - offset
    }
    attachToEdge(nextEdge.getId)
    setPosition(pos)
  }

  override def setPosition(pct: Double): Unit = {
    positionPct = pct
    super.setPosition(pct)
  }

  def getPosition: Double = positionPct

  /** Move in larger percentage steps across shorter edges */
  private def calculateIncrement(edge: Edge, deltaTime: Double): Double = {
    val edgeLen = edge.getAttribute("length", classOf[Object]).asInstanceOf[Double]
    deltaTime * speed * SCALE / edgeLen
  }

  /**
   * select an edge other than the one we came from
   */
  private def randomEdge(node: Node) = {
    val rand = (Math.random * node.getOutDegree).toInt
    node.getLeavingEdge(rand)
  }
}