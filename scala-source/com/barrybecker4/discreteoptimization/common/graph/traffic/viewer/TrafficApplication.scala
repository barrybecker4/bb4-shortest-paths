package com.barrybecker4.discreteoptimization.common.graph.traffic.viewer

import com.barrybecker4.discreteoptimization.common.graph.traffic.demo.TrafficDemo


object TrafficApplication {
  private val SPRITE_COUNT = 80

  def main(args: Array[String]): Unit = {
    System.setProperty("org.graphstream.ui", "org.graphstream.ui.swing.util.Display")

    val graph = new TrafficGraphGenerator().generateGraph()
    new TrafficDemo(graph, SPRITE_COUNT).run()
  }
}
