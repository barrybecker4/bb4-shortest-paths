package com.barrybecker4.discreteoptimization

import com.barrybecker4.discreteoptimization.AppUtil.getFileName
import com.barrybecker4.discreteoptimization.common.model.graph.directed.DirectedGraph
import com.barrybecker4.discreteoptimization.shortestpaths.model.DirectedGraphParser
import com.barrybecker4.discreteoptimization.kshortestpaths.solver.KShortestPathsSolver
import com.barrybecker4.discreteoptimization.kshortestpaths.solver.GreedyKPathsSolver

import java.io.*
import java.util
import scala.io.Source


/** Some ideas for solving graph coloring
 * - Brelaz heuristic, also called DSatur, See https://en.wikipedia.org/wiki/DSatur
 * - branch and cut
 * - branch and price
 * - Integer Linear Programming
 * - backtracking (like Brelaz heuristic)
 */
object KShortestPathsApp {

  /** Read the instance, solve it, and print the solution in the standard output
    */
  def main(args: Array[String]): Unit = {
    val fileName = getFileName(args)
    val graph: DirectedGraph = DirectedGraphParser().parse(fileName)

    val solution = GreedyKPathsSolver(2).findPaths(graph, 0, 4, 3)

    println(solution.toString)
  }

}
