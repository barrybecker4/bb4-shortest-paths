package com.barrybecker4.discreteoptimization.common.graph.traffic.demo

import com.barrybecker4.discreteoptimization.common.graph.traffic.viewer.TrafficGraphGenerator
import org.graphstream.graph.Graph
import org.graphstream.ui.view.{Viewer, ViewerPipe}
import com.barrybecker4.discreteoptimization.common.graph.traffic.demo.ViewerAdapter
import com.barrybecker4.discreteoptimization.common.graph.traffic.vehicles.VehicleSpriteGenerator
import com.barrybecker4.discreteoptimization.common.graph.visualization.render.GraphViewerPipe
import org.graphstream.graph.implementations.MultiGraph

import java.awt.{Dimension, Frame}
import javax.swing.JFrame


class TrafficDemo(graph: Graph, numSprites: Int, viewerPipe: ViewerPipe) {
  final private val viewerListener = new ViewerAdapter
  final private val spriteGenerator: VehicleSpriteGenerator = new VehicleSpriteGenerator(numSprites)

  def run(): Unit = {
    //val graph = new TrafficGraphGenerator().generateGraph()
    //val viewer = graph.display(false)
    //setViewerSize(1200, 1000, viewer)
    //val pipeIn = viewer.newViewerPipe
    val pipeIn = new GraphViewerPipe("my pipe", viewerPipe)
    pipeIn.addViewerListener(viewerListener)
    pipeIn.pump()
    sleep(1000) // give a chance to layout

    spriteGenerator.addSprites(graph)
    simulateTrafficFlow(pipeIn)
  }

  private def simulateTrafficFlow(pipeIn: ViewerPipe): Unit = {
    while (viewerListener.isLooping) {
      pipeIn.pump()
      spriteGenerator.moveSprites()
      sleep(10)
    }
    System.exit(0)
  }

//  private def setViewerSize(width: Int, height: Int, viewer: Viewer): Unit = {
//    viewer.getDefaultView.openInAFrame(true)
//    val frame = Frame.getFrames()(Frame.getFrames.length - 1)
//    frame.setPreferredSize(new Dimension(width, height))
//    frame.pack()
//    frame.setVisible(true)
//  }

  private def sleep(ms: Long): Unit = {
    try Thread.sleep(ms)
    catch {
      case e: InterruptedException =>
        e.printStackTrace()
    }
  }
}
