package com.barrybecker4.discreteoptimization.traffic.signals

import com.barrybecker4.discreteoptimization.traffic.signals.LightState
import com.barrybecker4.discreteoptimization.traffic.vehicles.VehicleSprite
import org.graphstream.graph.Node


trait TrafficSignal {

  def getOptimalDistance: Double = 3.0
  def getFarDistance: Double = 30.0
  def getYellowDurationSecs: Int = 3
  def getGreenDurationSecs: Int = 7
  def getLightState(port: Int): LightState

  def handleTraffic(sortedVehicles: IndexedSeq[VehicleSprite], portId: Int,
                    node: Node, edgeLen: Double, deltaTime: Double): Unit
  def showLight(node: Node, lightState: LightState): Unit = {
    node.setAttribute("ui.style", "size: 30px; z-index:0; fill-color: " + lightState.color)
  }

}
