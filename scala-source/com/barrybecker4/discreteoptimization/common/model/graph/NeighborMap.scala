package com.barrybecker4.discreteoptimization.common.model.graph

import scala.collection.immutable.Map

class NeighborMap {

  private var map: Map[Int, Set[Int]] = Map()

  def apply(v: Int): Set[Int] = map(v) 

  def addNeighbor(v1: Int, v2: Int): Unit = {
    if (map.contains(v1))
      map += v1 -> (map(v1) + v2)
    else
      map += v1 -> Set(v2)
  }
}
