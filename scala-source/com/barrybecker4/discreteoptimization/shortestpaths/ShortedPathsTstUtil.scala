package com.barrybecker4.discreteoptimization.shortestpaths

import com.barrybecker4.discreteoptimization.common.graph.directed.DirectedGraph
import com.barrybecker4.discreteoptimization.shortestpaths.model.{DirectedGraphParser, ShortestPathsSolution, ShortestPathsSolutionParser}

import java.io.{File, PrintWriter}
import scala.io.Source

object ShortedPathsTstUtil {

  val PREFIX = "scala-test/com/barrybecker4/discreteoptimization/shortestpaths/solver/data/"
  val PARSER: DirectedGraphParser = DirectedGraphParser()

  def getGraph(name: String): DirectedGraph = {
    val source: Source = Source.fromFile(PREFIX + name)
    PARSER.parse(source, name)
  }


  def getSerializedSolution(name: String): String = {
    val source: Source = Source.fromFile(PREFIX + name)
    val s = source.getLines().mkString("\n") + "\n"
    source.close()
    s
  }

  def getSolution(name: String): ShortestPathsSolution = {
    val source: Source = Source.fromFile(PREFIX + name)
    ShortestPathsSolutionParser().parse(source.getLines().toIndexedSeq)
  }

  def writeSolution(name: String, text: String): Unit = {
    val pw = new PrintWriter(new File(PREFIX + name))
    pw.write(text)
    pw.close()
  }
}
