package com.barrybecker4.discreteoptimization.traffic.viewer.adapter

import com.barrybecker4.discreteoptimization.common.Location
import com.barrybecker4.discreteoptimization.traffic.graph.model.Intersection
import com.barrybecker4.discreteoptimization.traffic.signals.TrafficSignal
import com.barrybecker4.discreteoptimization.traffic.vehicles.VehicleSprite
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

  // Called by the orchestrator to update the intersection every timestep
  // * - Within an intersection, examine the sprites on intersection edges and the edges leading into the intersection.
  // * - Sprites should be aware of how distant the next sprite in front is, if any.
  // *     - There should be an optimal distance to it
  // *     - If >= distantThreshold, don't try to catch up
  // *     - If < distanceThreshold, and > optimalDistance, then try to speed up a little to get closer to optimal
  // *     - If < optimalDistance, then break until >= optimalDistance
  // *     - If Signal says to slow down, then brake to slow speed
  // *     - If upcoming Signal is red, then start to smoothly slow so that we can be stopped by the time we get there
  def update(deltaTime: Double): Unit = {
    // println("Updating intersection...  " + deltaTime)
    // For incoming edge
    //   try to get the vehicles to be an optimal distance apart by adjusting their speed
    for (portId <- intersection.ports.indices) {
      val node: Node = getIncomingNode(portId)
      assert(node.getInDegree == 1)
      val edge: Edge = node.getEnteringEdge(0)
      //val sprites = edge.getAttribute("vehicles", classOf[mutable.Queue[VehicleSprite]])
      //println("edge vehicles = " + sprites.toString)
    }
  }
}
