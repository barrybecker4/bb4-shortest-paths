package com.barrybecker4.discreteoptimization.traffic.viewer.adapter

import com.barrybecker4.discreteoptimization.common.Location
import com.barrybecker4.discreteoptimization.traffic.graph.model.Intersection
import com.barrybecker4.discreteoptimization.traffic.signals.TrafficSignal
import com.barrybecker4.discreteoptimization.traffic.vehicles.{VehicleSprite, VehicleSpriteManager}
import com.barrybecker4.discreteoptimization.traffic.viewer.adapter.IntersectionSubGraphBuilder
import org.graphstream.graph.{Edge, Node}
import org.graphstream.graph.implementations.MultiGraph

import scala.collection.mutable


object IntersectionSubGraph {
}

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
    // println("Updating intersection...  " + deltaTime)
    // For incoming edge
    //   try to get the vehicles to be an optimal distance apart by adjusting their speed
    for (portId <- intersection.ports.indices) {
      val node: Node = getIncomingNode(portId)
      assert(node.getInDegree == 1)
      val edge: Edge = node.getEnteringEdge(0)
      val edgeLen = edge.getAttribute("length", classOf[Object]).asInstanceOf[Double]
      // spriteManager.getVehiclesOnEdge(edgeId)
      val sprites = spriteManager.getVehiclesOnEdge(edge.getId) //edge.getAttribute("vehicles", classOf[mutable.PriorityQueue[VehicleSprite]])
      assert (sprites != null, "sprites should not be null")
      if (Math.random() < 0.005)
        println(s"edge vehicles on edge ${edge.getId} = " + sprites.map(s => s.getId))

      var nextSprite: VehicleSprite = null
      val sortedSprites: Seq[VehicleSprite] = sprites.clone().dequeueAll
      sortedSprites.foreach(sprite => {
        if (nextSprite != null) {
          val distanceToNext = (nextSprite.getPosition - sprite.getPosition) * edgeLen
          println(s"distToNext=$distanceToNext optDist=${signal.getOptimalDistance}")
          // assert(distanceToNext > 0, "The distance to the car in front should never be less that 0")         // Hitting this
          if (distanceToNext < signal.getFarDistance) {
            if (distanceToNext < signal.getOptimalDistance) {
              sprite.changeSpeed(-0.02)
            } else if (sprite.getSpeed < nextSprite.getSpeed) {
              sprite.changeSpeed(0.01)
            }
          }
        }
        nextSprite = sprite
      })
    }
  }
}
