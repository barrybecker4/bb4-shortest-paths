package com.barrybecker4.discreteoptimization.shortestpaths

import com.barrybecker4.discreteoptimization.common.graph.GraphTstUtil
import com.barrybecker4.discreteoptimization.common.graph.visualization.{GraphStreamAdapter, GraphViewerFrame}


object GraphPathViewerApp extends App {
  
  val digraph = GraphTstUtil.getGraph("sp_10_1") // switch to ShortesPathsTstUtil
  val graph = GraphStreamAdapter(digraph).createGraph()

  val frame = new GraphPathViewerFrame(graph, "sp_10_1")
}
