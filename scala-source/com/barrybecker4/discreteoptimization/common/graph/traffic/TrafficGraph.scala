package com.barrybecker4.discreteoptimization.common.graph.traffic

import com.barrybecker4.discreteoptimization.common.Location
import com.barrybecker4.discreteoptimization.common.graph.NeighborMap
import com.barrybecker4.discreteoptimization.common.graph.traffic.{Intersection, Street}


/**
 * An immutable directed graph with optional node locations
 */
case class TrafficGraph(intersections: IndexedSeq[Intersection], streets: IndexedSeq[Street]) {
  
  private val neighborMap: NeighborMap = NeighborMap()
  computeNeighborsMap()

  def numIntersections: Int = intersections.size
  def getLocation(intersectionIdx: Int): Location = intersections(intersectionIdx).location
  def getIntersection(intersectionIdx: Int): Intersection = intersections(intersectionIdx)
  def neighborsOf(v: Int): Set[Intersection] = neighborMap(v).map(i => intersections(i))
  
  private def computeNeighborsMap(): Unit = {
    for (street <- streets) {
      neighborMap.addNeighbor(street.intersectionIdx1, street.intersectionIdx2)
    }
  }

}
