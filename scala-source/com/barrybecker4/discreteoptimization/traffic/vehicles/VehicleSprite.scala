package com.barrybecker4.discreteoptimization.traffic.vehicles

import com.barrybecker4.discreteoptimization.traffic.vehicles.VehicleSprite.{DEBUG, MAX_ACCELERATION, MAX_SPEED, RND}
import org.graphstream.graph.Edge
import org.graphstream.graph.Node
import org.graphstream.ui.spriteManager.Sprite
import com.barrybecker4.discreteoptimization.traffic.vehicles.VehicleSpriteManager

import scala.util.Random


object VehicleSprite {
  // Meters/second
  private val MAX_SPEED = 20.0
  // Meters/ second^2
  private val MAX_ACCELERATION = 5.0
  private val DEBUG = false
  private val RND = Random(0)
}

/**
 * @param identifier name of the sprite
 * @param initialSpeed initial speed of the sprite in meters per second
 */
class VehicleSprite(identifier: String, initialSpeed: Double, manager: VehicleSpriteManager, rnd: Random = RND) extends Sprite(identifier, manager) {
  private var positionPct: Double = 0.0 // 0 - 1.0
  private var speed = initialSpeed

  def getSpeed: Double = speed
  def getCurrentEdge: Edge = getAttachment.asInstanceOf[Edge]

  /** @param acceleration requested amount of acceleration to change the current speed by. It has constraints.
   */
  def accelerate(acceleration: Double): Unit = {
    speed += Math.max(-MAX_ACCELERATION, Math.min(acceleration, MAX_ACCELERATION))
    speed = Math.max(0, Math.min(speed, MAX_SPEED))
  }

  def setSpeed(newSpeed: Double): Unit = {
    speed = Math.max(0, Math.min(newSpeed, MAX_SPEED))
  }

  def brake(stoppingDistance: Double, deltaTime: Double): Unit = {
    val brake = Math.min(MAX_ACCELERATION, speed * deltaTime / stoppingDistance) // not sure about this
    // println("braking by " + brake + ";  stoppingDistance=" + stoppingDistance + " deltaTime=" + deltaTime + " speed=" + speed)
    accelerate(-brake)
  }

  // This would be quite jarring to the driver. Avoid doing this unless going slow.
  def stop(): Unit = {
    speed = 0
  }

  override def attachToEdge(edgeId: String): Unit = {
    val edge = manager.getEdge(edgeId)
    if (this.attachment != edge) {
      this.detach()
      this.attachment = edge
      manager.addVehicleToEdge(edgeId, this)
    }
    this.attachment.setAttribute(this.completeId, Array(0: java.lang.Double))
  }

  override def detach(): Unit = {
    if (getCurrentEdge != null) {
      manager.removeVehicleFromEdge(getCurrentEdge.getId, this)
      super.detach()
    }
  }
  
  def move(deltaTime: Double): Unit = {
    var p: Double = getPosition
    val step = calculateIncrement(getCurrentEdge, deltaTime)
    p += step
    if (p < 0 || p > 1)
      chooseNextEdge(p, step, deltaTime)
    else setPosition(p)

    val edgeId = getCurrentEdge.getId
    if (DEBUG && (this.getId == "60" || edgeId == "i2:p0-i1:p0"))
      setAttribute("ui.label", s"id: $getId pct: ${positionPct.toFloat}        s: ${speed.toFloat} edge:${edgeId}")
    else
      setAttribute("ui.label", "")
  }
  
  def predictNextPosition(deltaTime: Double): Double = {
    getPosition + calculateIncrement(getAttachment.asInstanceOf[Edge], deltaTime)
  }

  def chooseNextEdge(p: Double, step: Double, deltaTime: Double): Unit = {
    val edge = getCurrentEdge
    var node = edge.getSourceNode

    if (step > 0) node = edge.getTargetNode
    val nextEdge = randomEdge(node)
    val offset: Double = Math.abs(p % 1)
    val pos = if (node eq nextEdge.getSourceNode) {
      offset
    } else {
      // For the traffic sim, we never do this, because the vehicles always move forward.
      1.0 - offset
    }
    attachToEdge(nextEdge.getId)
    setPosition(pos)
  }

  override def setPosition(pct: Double): Unit = {
    if (positionPct != pct) {
      positionPct = pct
      super.setPosition(pct)
    }
  }

  def getPosition: Double = positionPct

  /** Move in larger percentage steps across shorter edges */
  private def calculateIncrement(edge: Edge, deltaTime: Double): Double = {
    val edgeLen = edge.getAttribute("length", classOf[Object]).asInstanceOf[Double]
    deltaTime * speed / edgeLen
  }
  /**
   * select an edge other than the one we came from
   */
  private def randomEdge(node: Node): Edge = {
    val rand = rnd.nextInt(node.getOutDegree)
    node.getLeavingEdge(rand)
  }
}