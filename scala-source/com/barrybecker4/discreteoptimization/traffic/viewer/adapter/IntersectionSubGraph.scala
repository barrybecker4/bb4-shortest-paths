package com.barrybecker4.discreteoptimization.traffic.viewer.adapter

import com.barrybecker4.discreteoptimization.common.Location
import com.barrybecker4.discreteoptimization.traffic.graph.model.Intersection
import com.barrybecker4.discreteoptimization.traffic.signals.TrafficSignal
import com.barrybecker4.discreteoptimization.traffic.vehicles.{VehicleSprite, VehicleSpriteManager}
import com.barrybecker4.discreteoptimization.traffic.viewer.adapter.IntersectionSubGraphBuilder
import org.graphstream.graph.{Edge, Node}
import org.graphstream.graph.implementations.MultiGraph
import com.barrybecker4.discreteoptimization.traffic.signals.LightState.*

import scala.collection.mutable


/**
 * Pupulates the graph with all the nodes and edges needed to represent the N-way intersection.
 * Provides convenient accessors for the streets that enter and exit the intersection.
 */
case class IntersectionSubGraph(intersection: Intersection, signal: TrafficSignal, graph: MultiGraph) {

  private val builder = new IntersectionSubGraphBuilder(intersection, graph)

  def getIncomingNode(portId: Int): Node = builder.incomingNodes(portId)
  def getOutgoingNode(portId: Int): Node = builder.outgoingNodes(portId)


  // Called by the orchestrator to update the intersection every timeStep
  // * - Within an intersection, examine the sprites on intersection edges and the edges leading into the intersection.
  // * - Sprites should be aware of how distant the next sprite in front is, if any.
  // *     - There should be an optimal distance to it
  // *     - If >= distantThreshold, don't try to catch up
  // *     - If < distanceThreshold, and > optimalDistance, then try to speed up a little to get closer to optimal
  // *     - If < optimalDistance, then break until >= optimalDistance
  // *     - If Signal says to slow down, then brake to slow speed
  // *     - If upcoming Signal is red, then start to smoothly slow so that we can be stopped by the time we get there
  def update(deltaTime: Double, spriteManager: VehicleSpriteManager): Unit = {
    // For incoming edge
    //   try to get the vehicles to be an optimal distance apart by adjusting their speed
    //println(" -- now updating vehicle positions on edges of intersection " + intersection.id)
    for (portId <- intersection.ports.indices) {
      val node: Node = getIncomingNode(portId)
      assert(node.getInDegree == 1)
      val edge: Edge = node.getEnteringEdge(0)
      val edgeLen = edge.getAttribute("length", classOf[Object]).asInstanceOf[Double]
      val sprites = spriteManager.getVehiclesOnEdge(edge.getId)

      if (sprites.nonEmpty) {
        val sortedSprites: IndexedSeq[VehicleSprite] = sprites.toIndexedSeq.sortBy(-_.getPosition)
        var nextSprite: VehicleSprite = null
        //handleTrafficSignal(sortedSprites, portId, edgeLen, deltaTime)
        sortedSprites.foreach(sprite => {
          if (nextSprite != null) {
            val distanceToNext = (nextSprite.getPosition - sprite.getPosition) * edgeLen
            assert(distanceToNext > 0, "The distance to the car in front should never be less than 0")
            // the following is not working as expected
            if (distanceToNext < signal.getFarDistance) {
              if (distanceToNext < signal.getOptimalDistance) {
                sprite.brake(distanceToNext / 2.0, deltaTime)
              } else if (sprite.getSpeed <= nextSprite.getSpeed) {
                sprite.changeSpeed(0.1)
              }
            }
          }
          nextSprite = sprite
        })
      }
    }
  }

  /**
   * if the light is red, then first car should already be stopped
   * if the light is yellow,
   *   then all cars closer than speed * yellowTime should continue
   *   then the first car further than speed * yellowTime should prepare to stop
   * if the light is green, then all cars should continue
   *
   * sprites with text
   * draw straight edges
   * draw the lights at intersection nodes
   * sprite orientation - projection
   */
  private def handleTrafficSignal(sortedVehicles: IndexedSeq[VehicleSprite], portId: Int, edgeLen: Double, deltaTime: Double): Unit = {
    val signalStatus = signal.getLightState(portId)
    val vehicleClosestToLight = sortedVehicles.head
    if (signalStatus == RED) {
      // if the light is red, then first car should already be stopped
      if (vehicleClosestToLight.getSpeed > 0.0)
        println("vehicleClosestToLight.getSpeed=" + vehicleClosestToLight.getSpeed + " should have been 0")
      vehicleClosestToLight.stop()
    } else if (signalStatus == YELLOW) {
      val yellowTime = signal.getYellowDurationSecs.toDouble
      var vehicleIdx = 0
      var vehicle = vehicleClosestToLight
      while ((1.0 - vehicle.getPosition) * edgeLen < yellowTime * vehicle.getSpeed && vehicleIdx > 0) {
        vehicleIdx += 1
        vehicle = sortedVehicles(vehicleIdx)
      }
      vehicle.brake(yellowTime * vehicle.getSpeed * .9, deltaTime)
    } else if (signalStatus == GREEN) {
      vehicleClosestToLight.changeSpeed(0.2)
    }
  }
}
