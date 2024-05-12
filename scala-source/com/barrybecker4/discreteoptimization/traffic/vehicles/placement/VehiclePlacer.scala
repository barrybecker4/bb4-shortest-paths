package com.barrybecker4.discreteoptimization.traffic.vehicles.placement

import org.graphstream.graph.Edge
import org.graphstream.graph.Graph
import org.graphstream.ui.spriteManager.Sprite
import org.graphstream.ui.spriteManager.SpriteManager

import java.util.concurrent.atomic.AtomicInteger
import scala.util.Random


/**
 * Determines vehicle placements along all the edges of a provided graph.
 * We don't want the vehicles placed perfectly uniformly because that would not be natural.
 * Instead, they should be placed randomly, but with no overlap.
 * Throw an error if there are so many vehicles that they cannot be placed without overlap.
 * Each edge will have a maximum number of vehicles that it can hold. Do not exceed that.
 */
object VehiclePlacer {
  /** The minimum gap between vehicles */
  private val MIN_GAP = 15.0
  /**
   * This will not be exact because the sprites size is relative to the window size.
   * Sprites get proportionally larger as the window size shrinks.
   * See traffic.css - size: 12px, 6px;
   */
  private val VEHICLE_LENGTH = 30.0
  private val VEHICLE_COLORS = Array[String]("#aa5588;", "#11bb99;", "#5522aa;")
}

class VehiclePlacer(private val sprites: SpriteManager, private val graph: Graph) {
  final private var rnd: Random = new Random(0)

  def placeVehicleSprites(): Unit = {
    val edgeIdToNumVehicles = determineNumVehiclesOnEdges
    allocateVehicles(edgeIdToNumVehicles)
  }

  /**
   * Each edge can have an expected allocation assuming uniform distribution.
   * expectedAllocation = edgeLen / totalLen * numVehicles.
   * Each edge can support a maximum number of vehicles
   * maxAllocation = floor(edgeLen / (MIN_GAP + vehicleLen))
   * If expectedAllocation > maxAllocation for any edge, then throw error.
   *
   * The actual number of vehicles to put on an edge can be
   * delta = maxAllocation - expectedAllocation
   * random( expectedAllocation - delta, expectedAllocation + delta + 1)
   * Then if there are too few allocated, randomly add them from edges until numVehicles reached.
   * If too many allocation, randomly remove them from edges until numVehicles reached.
   */
  private def determineNumVehiclesOnEdges = {
    val numVehicles = sprites.getSpriteCount
    val totalLen = findTotaLengthOfAllEdges(graph)
    var edgeIdToNumVehicles: Map[String, Integer] = Map()
    var totalAllocation: Integer = 0
    graph.edges.forEach((edge: Edge) => {
      val edgeId = edge.getId
      val edgeLen = getEdgeLen(edge)
      val expectedAllocation = (numVehicles * edgeLen / totalLen).toInt
      val maxAllocation = (edgeLen / (VehiclePlacer.MIN_GAP + VehiclePlacer.VEHICLE_LENGTH)).toInt
      if (expectedAllocation > maxAllocation) throw new IllegalArgumentException("Trying to allocate more vehicles (" + expectedAllocation + ") than the streets will hold (" + maxAllocation + ")!")
      edge.setAttribute("maxAllocation", maxAllocation)
      val delta = expectedAllocation
      assert(delta >= 0)
      val min = Math.max(0, expectedAllocation - delta)
      val max = Math.min(maxAllocation, min + 2 * delta)
      val randomAllocation = min + rnd.nextInt(max - min + 1)
      //System.out.println("randomAll for edge " + edgeId + " = " + randomAllocation + " (min=" + min + " max = " + max + ") expAllocation = " + expectedAllocation + " totalLen=" + totalLen) ;
      assert(randomAllocation <= maxAllocation)
      totalAllocation += randomAllocation
      edgeIdToNumVehicles += (edgeId, randomAllocation)
    })
    val edgeIds = edgeIdToNumVehicles.keySet.toArray
    // now do some fine-tuning in the event that we have too many or too few vehicles allocated
    while (totalAllocation > numVehicles) {
      val rndId = edgeIds(rnd.nextInt(edgeIds.length))
      if (edgeIdToNumVehicles(rndId) > 0) {
        edgeIdToNumVehicles += (rndId, edgeIdToNumVehicles(rndId) - 1)
        totalAllocation -= 1
      }
    }
    while (totalAllocation < numVehicles) {
      val rndId = edgeIds(rnd.nextInt(edgeIds.length))
      if (edgeIdToNumVehicles(rndId) < getMaxAllocation(graph.getEdge(rndId))) {
        edgeIdToNumVehicles += (rndId, edgeIdToNumVehicles(rndId) + 1)
        totalAllocation += 1
      }
    }
    val sumAllocatedVehicles = getSumAllocatedVehicles(edgeIdToNumVehicles)
    assert(numVehicles == sumAllocatedVehicles)
    edgeIdToNumVehicles
  }

  private def getSumAllocatedVehicles(edgeIdToNumVehicles: Map[String, Integer]) =
    edgeIdToNumVehicles.values.map(_.toInt).sum // Map values to integers.sum

  /**
   * Maintain a map from edgeId to num vehicles to allocate to it.
   * Allocation algorithm
   *  - Divide the edge into maxAllocation equal slots.
   *  - Create an array of that same length
   *  - Using vehicle hash mod maxVehicles for that edge,
   *    place each into the array using array hashing algorithm,
   *    which resolves conflicts by moving to the next available slot.
   *  - Place the sprites in the array into the edge at the corresponding position.
   */
  private def allocateVehicles(edgeIdToNumVehicles: Map[String, Integer]): Unit = {
    val spriteCt = new AtomicInteger
    graph.edges.forEach((edge: Edge) => {
      val numVehiclesToAdd = edgeIdToNumVehicles(edge.getId)
      placeVehiclesForEdge(edge, numVehiclesToAdd, spriteCt)
    })
  }

  private def placeVehiclesForEdge(edge: Edge, numVehiclesToAdd: Int, spriteCt: AtomicInteger): Unit = {
    val maxAllocation = getMaxAllocation(edge)
    val spriteSlots = new Array[Sprite](maxAllocation)
    assert(numVehiclesToAdd <= spriteSlots.length)
    //System.out.println("now adding " + numVehiclesToAdd + " to edge " + edge.getId() + " total avail slots = " + spriteSlots.length);
    for (i <- 0 until numVehiclesToAdd) {
      var positionIdx = rnd.nextInt(spriteSlots.length)
      while (spriteSlots(positionIdx) != null) positionIdx = (positionIdx + 1) % spriteSlots.length
      val sprite = sprites.addSprite(spriteCt.get + "")
      sprite.setAttribute("ui.style", "fill-color: " + VehiclePlacer.VEHICLE_COLORS((Math.random * VehiclePlacer.VEHICLE_COLORS.length).toInt))
      spriteCt.getAndIncrement
      val pos = positionIdx.toDouble / spriteSlots.length
      sprite.setPosition(pos)
      spriteSlots(positionIdx) = sprite
      sprite.attachToEdge(edge.getId)
    }
  }

  private def findTotaLengthOfAllEdges(graph: Graph) = graph.edges.mapToDouble(e => getEdgeLen(e)).toArray.sum

  private def getMaxAllocation(edge: Edge) = edge.getAttribute("maxAllocation", classOf[Object]).asInstanceOf[Integer]

  private def getEdgeLen(edge: Edge) =
    edge.getAttribute("length", classOf[Object]).asInstanceOf[Double]
}
