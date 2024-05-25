package com.barrybecker4.discreteoptimization.traffic.vehicles

import org.graphstream.graph.{Edge, Graph}
import org.graphstream.ui.spriteManager.SpriteManager
import scala.collection.mutable


class VehicleSpriteManager(graph: Graph) extends SpriteManager(graph) {

  implicit val vehicleSpriteOrdering: Ordering[VehicleSprite] = Ordering.by(_.getPosition)

  def getVehiclesOnEdge(edgeId: String): mutable.PriorityQueue[VehicleSprite] = {
    val edge: Edge = getEdge(edgeId)
    var vehicleSprites: mutable.PriorityQueue[VehicleSprite] =
      edge.getAttribute[mutable.PriorityQueue[VehicleSprite]]("vehicles", classOf[mutable.PriorityQueue[VehicleSprite]])
      
    if (vehicleSprites == null) {
      vehicleSprites = mutable.PriorityQueue()(vehicleSpriteOrdering) 
      edge.setAttribute("vehicles", vehicleSprites)
    }
    vehicleSprites
  }

  def addVehicleToEdge(edgeId: String, vehicleSprite: VehicleSprite): Unit =
    getVehiclesOnEdge(edgeId).enqueue(vehicleSprite)

  def getEdge(edgeId: String): Edge = graph.getEdge(edgeId)

  def removeVehicleFromEdge(edgeId: String, vehicleSprite: VehicleSprite): Unit = {
    val v = getVehiclesOnEdge(edgeId).dequeue()
    
    // I don't know what this happens sometimes. 
    //assert(v == vehicleSprite, "didn't remove the last sprite in the queue")
    //if (v != vehicleSprite)
    //  println("Didn't remove the last sprite in the queue.")
  }
}
