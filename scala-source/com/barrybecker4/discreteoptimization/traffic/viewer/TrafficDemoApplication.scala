package com.barrybecker4.discreteoptimization.traffic.viewer

import com.barrybecker4.discreteoptimization.common.graph.visualization.render.GraphViewerPipe
import com.barrybecker4.discreteoptimization.traffic.demo.TrafficDemo


/**
 * This just shows the hardcoded graph from TrafficGraphGenerator.
 * For general traffic map configurations, use TrafficViewerApp.
 */
object TrafficDemoApplication {
  private val SPRITE_COUNT = 100

  def main(args: Array[String]): Unit = {
    System.setProperty("org.graphstream.ui", "org.graphstream.ui.swing.util.Display")

    val graph = new TrafficGraphGenerator().generateGraph()
    val pipeIn = graph.display(false).newViewerPipe()
    new TrafficDemo(graph, SPRITE_COUNT, pipeIn).run()
  }
}
