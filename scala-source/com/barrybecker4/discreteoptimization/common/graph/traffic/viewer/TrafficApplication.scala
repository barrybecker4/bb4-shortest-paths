package com.barrybecker4.discreteoptimization.common.graph.traffic.viewer

import com.barrybecker4.discreteoptimization.common.graph.traffic.demo.TrafficDemo
import com.barrybecker4.discreteoptimization.common.graph.visualization.render.GraphViewerPipe


/**
 * This just shows the hardcoded graph from TrafficGraphGenerator.
 * For general traffic map configurations, use TrafficViewerApp.
 */
object TrafficApplication {
  private val SPRITE_COUNT = 80

  def main(args: Array[String]): Unit = {
    System.setProperty("org.graphstream.ui", "org.graphstream.ui.swing.util.Display")

    val graph = new TrafficGraphGenerator().generateGraph()
    val pipeIn = graph.display(false).newViewerPipe()
    new TrafficDemo(graph, SPRITE_COUNT, pipeIn).run()
  }
}
