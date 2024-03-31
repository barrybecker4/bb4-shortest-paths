package com.barrybecker4.discreteoptimization.common.graph.directed

import com.barrybecker4.discreteoptimization.common.Location

case class Nodes(numNodes: Int, locations: Option[Array[Location]] = None, weights: Option[Array[Double]]) {

  def this(numNodes: Int) = this(numNodes, None, None)

  def hasLocations: Boolean = locations.nonEmpty
  def hasWeights: Boolean = weights.nonEmpty

  def getWeight(nodeIdx: Int): Double = if (weights.isEmpty) 0 else weights.get(nodeIdx)
}
