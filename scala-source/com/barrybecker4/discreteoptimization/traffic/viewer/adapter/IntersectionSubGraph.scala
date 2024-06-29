package com.barrybecker4.discreteoptimization.traffic.viewer.adapter

import com.barrybecker4.common.format.FormatUtil
import com.barrybecker4.discreteoptimization.common.Location
import com.barrybecker4.discreteoptimization.traffic.graph.model.Intersection
import com.barrybecker4.discreteoptimization.traffic.signals.TrafficSignal
import com.barrybecker4.discreteoptimization.traffic.vehicles.{VehicleSprite, VehicleSpriteManager}
import com.barrybecker4.discreteoptimization.traffic.viewer.adapter.IntersectionSubGraphBuilder
import org.graphstream.graph.{Edge, Node}
import org.graphstream.graph.implementations.MultiGraph
import com.barrybecker4.discreteoptimization.traffic.signals.LightState.*
import com.barrybecker4.discreteoptimization.traffic.vehicles.VehicleSprite.DEBUG
import com.barrybecker4.common.format.FormatUtil.formatNumber

import scala.collection.mutable


/**
 * Represents the nodes and edges in an intersection.
 * Regulates the movement of vehicles on the edges leading into the intersection
 */
case class IntersectionSubGraph(intersection: Intersection, signal: TrafficSignal, graph: MultiGraph) {

  private val builder = new IntersectionSubGraphBuilder(intersection, graph)

  def getIncomingNode(portId: Int): Node = builder.incomingNodes(portId)
  def getOutgoingNode(portId: Int): Node = builder.outgoingNodes(portId)


  /** Called by the orchestrator to update the intersection every timeStep
   * - Within an intersection, examine the sprites on intersection edges and the edges leading into the intersection.
   * - Sprites should be aware of how distant the next sprite in front is, if any.
   *     - There should be an optimal distance to it
   *     - If >= distantThreshold, don't try to catch up
   *     - If < distanceThreshold, and > optimalDistance, then try to speed up a little to get closer to optimal
   *     - If < optimalDistance, then break until >= optimalDistance
   *     - If Signal says to slow down, then brake to slow speed
   *     - If upcoming Signal is red, then start to smoothly slow so that we can be stopped by the time we get there
   * Under no circumstances should a vehicle be able to pass another.
   */
  def update(deltaTime: Double, spriteManager: VehicleSpriteManager): Unit = synchronized {
    for (portId <- intersection.ports.indices) {
      val inNode: Node = getIncomingNode(portId)
      signal.showLight(inNode, portId)
      assert(inNode.getInDegree == 1, "There should be exactly one edge entering the intersection on a port")
      val incomingEdge: Edge = inNode.getEnteringEdge(0)
      updateVehiclesOnEdge(true, incomingEdge, portId, deltaTime, spriteManager)
    }
  }

  private def updateVehiclesOnEdge(handleSignal: Boolean, edge: Edge, portId: Int, deltaTime: Double,
                                   spriteManager: VehicleSpriteManager): Unit = {
    val edgeLen = edge.getAttribute("length", classOf[Object]).asInstanceOf[Double]
    val sprites = spriteManager.getVehiclesOnEdge(edge.getId)

    if (sprites.nonEmpty) {
      val sortedSprites: IndexedSeq[VehicleSprite] = sprites.toIndexedSeq.sortBy(_.getPosition)
      var nextSprite: VehicleSprite = null
      if (handleSignal)
        signal.handleTraffic(sortedSprites, portId, edgeLen, deltaTime)
      for (i <- 0 until sortedSprites.size - 1) {
        val sprite = sortedSprites(i)
        val nextSprite = sortedSprites(i + 1)
        val distanceToNext = (nextSprite.getPosition - sprite.getPosition) * edgeLen
        assert(distanceToNext > 0, "The distance to the car in front should never be less than 0")
        if (distanceToNext < signal.getFarDistance) {
          if (distanceToNext < signal.getOptimalDistance) {
            if (sprite.getSpeed >= nextSprite.getSpeed) {
              //println(s"sprite slowed from ${sprite.getSpeed} to ${nextSprite.getSpeed * 0.8}")
              sprite.setSpeed(nextSprite.getSpeed * 0.9)
            }
          }
          else if (sprite.getSpeed <= nextSprite.getSpeed) {
            sprite.setSpeed(nextSprite.getSpeed * 1.05)
          }
        }
      }
    }
  }
}
