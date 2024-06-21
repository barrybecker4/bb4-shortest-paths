package com.barrybecker4.discreteoptimization.traffic.signals

import com.barrybecker4.discreteoptimization.traffic.signals.LightState
import com.barrybecker4.discreteoptimization.traffic.vehicles.VehicleSprite
import org.graphstream.graph.Node


trait TrafficSignal {

  def getOptimalDistance: Double = 30.0
  def getFarDistance: Double = 200.0
  def getYellowDurationSecs: Int = 2
  def getGreenDurationSecs: Int = 4
  def getRedDurationSecs: Int
  def getLightState(port: Int): LightState

  def handleTraffic(sortedVehicles: IndexedSeq[VehicleSprite], portId: Int,
                    edgeLen: Double, deltaTime: Double): Unit
  def showLight(node: Node, portId: Int): Unit = {
    val lightState = getLightState(portId)
    node.setAttribute("ui.style", "size: 30px; z-index:0; fill-color: " + lightState.color)
  }

}
