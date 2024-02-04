package com.barrybecker4.discreteoptimization.common.graph.visualization

import com.barrybecker4.discreteoptimization.common.graph.GraphTstUtil
import com.barrybecker4.discreteoptimization.common.graph.visualization.{GraphStreamAdapter, GraphViewerFrame}


object GraphViewerApp extends App {
  
  val digraph = GraphTstUtil.getGraph("sp_10_1")
  val graph = GraphStreamAdapter(digraph).createGraph()

  val frame = new GraphViewerFrame(graph, "sp_10_1")
}
