package com.barrybecker4.discreteoptimization.shortestpaths

import com.barrybecker4.discreteoptimization.common.model.graph.visualization.{GraphStreamAdapter, GraphViewerFrame}


object GraphViewerApp extends App {
  
  val digraph = ShortedPathsTstUtil.getGraph("sp_10_1")
  val graph = GraphStreamAdapter(digraph).createGraph()

  val frame = new GraphViewerFrame(graph, "sp_10_1")
}
