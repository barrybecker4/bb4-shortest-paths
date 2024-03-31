package com.barrybecker4.discreteoptimization.common.graph.directed

import com.barrybecker4.discreteoptimization.common.graph.directed.{DirectedEdge, DirectedGraph, Nodes}
import com.barrybecker4.discreteoptimization.common.{Location, Parser}


/**
 * Parse directed graphs. The format is as follows
 * The first line consists of
 * <numVertices> <numEdges> <hasLocations> <hasNodeWeights>
 * The last 2, hasLocations and hasNodeWeights, are boolean and optional
 * The lines that follow consist of
 */
case class DirectedGraphParser() extends Parser[DirectedGraph] {
  
  override protected def parse(lines: IndexedSeq[String], problemName: String): DirectedGraph = {
    val firstLine = lines(0).split("\\s+")
    val numNodes = firstLine(0).toInt
    val numEdges = firstLine(1).toInt
    val hasLocations = if (firstLine.length > 2) firstLine(2).toBoolean else false
    val hasNodeWeights = if (firstLine.length > 3) firstLine(3).toBoolean else false
    
    val hasNodeData = hasLocations || hasNodeWeights
    val nodes =
      if (hasNodeData) parseNodes(numNodes, hasLocations, hasNodeWeights, lines)
      else new Nodes(numNodes)
    
    val start = if (hasNodeData) 1 + numNodes else 1
    val edges = parseEdges(start, numEdges, lines)

    DirectedGraph(nodes, edges)
  }

  private def parseNodes(numNodes: Int,
                         hasLocations: Boolean, hasWeights: Boolean, lines: IndexedSeq[String]): Nodes = {
    // var locations: Option[Array[Location]] = None
    val locations: Array[Location] = Array.fill(numNodes)(null)
    val weights: Array[Double] = Array.fill(numNodes)(0)
    for (i <- 0 until numNodes) {
      val line = lines(i + 1)
      val parts = line.split("\\s+")
      if (hasLocations)
        locations(i) = Location(parts(0).toFloat, parts(1).toFloat)
      if (hasWeights) {
        val wtPosition = if (hasLocations) 2 else 0
        weights(i) = parts(wtPosition).toDouble
      }  
    }
    Nodes(numNodes, Some(locations), Some(weights))
  }

  /**
   * Warn if there are eny duplicate edges. Normally, there should not be.
   * Just warn the first time to avoid too many warnings.
   */
  private def parseEdges(start: Int, numEdges: Int, lines: IndexedSeq[String]): IndexedSeq[DirectedEdge] = {
    var edges = IndexedSeq[DirectedEdge]()
    var edgeSet: Set[(Int, Int)] = Set()
    for (i <- 0 until numEdges) {
      val line = lines(i + start)
      val parts = line.split("\\s+")
      // if no weight specified, use a random one
      val weight = if (parts.length > 2) parts(2).toDouble else 1 + Math.random()
      val source = parts(0).toInt
      val dest = parts(1).toInt
      val e = (source, dest)
      
      if (edgeSet.contains(e)) {
        println(s"More than one edge from ${e._1} to ${e._2}. This is allowed, but may not be what you want.")
      } else edgeSet += (source, dest)
      
      edges :+= DirectedEdge(source, dest, weight)
    }
    edges
  }
}

