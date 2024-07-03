package com.barrybecker4.discreteoptimization.traffic.signals

import com.barrybecker4.discreteoptimization.traffic.signals.SignalState
import com.barrybecker4.discreteoptimization.traffic.signals.SignalState.{GREEN, RED, YELLOW}
import com.barrybecker4.discreteoptimization.traffic.vehicles.VehicleSprite
import org.graphstream.graph.Node


trait TrafficSignal(numStreets: Int) {

  def getOptimalDistance: Double = 30.0
  def getFarDistance: Double = 200.0
  def getYellowDurationSecs: Int = 2
  def getGreenDurationSecs: Int = 4
  def getLightState(port: Int): SignalState
  protected var yellowStartTime = 0L

  def handleTraffic(sortedVehicles: IndexedSeq[VehicleSprite], portId: Int,
                    edgeLen: Double, deltaTime: Double): Unit
  def showLight(node: Node, portId: Int): Unit = {
    val lightState = getLightState(portId)
    node.setAttribute("ui.style", "size: 30px; z-index:0; fill-color: " + lightState.color)
  }

  def printLightStates(): Unit = {
    val states = Range(0, numStreets).map(i => getLightState(i))
    println(states)
  }

  protected def handleTrafficBasedOnLightState(sortedVehicles: IndexedSeq[VehicleSprite],
                                               portId: Int, edgeLen: Double, deltaTime: Double): Unit = {
    val lightState = getLightState(portId)
    if (sortedVehicles.isEmpty) return
    val vehicleClosestToLight = sortedVehicles.last

    lightState match {
      case RED =>
        // if the light is red, then first car should already be stopped if it is close to the light
        if (vehicleClosestToLight.getSpeed > 0.0 && vehicleClosestToLight.getPosition > 0.96) {
          //println("vehicleClosestToLight.getSpeed=" + vehicleClosestToLight.getSpeed + " should have been 0")
          vehicleClosestToLight.stop()
        } else if (vehicleClosestToLight.getPosition > 0.85) {
          vehicleClosestToLight.setSpeed(vehicleClosestToLight.getSpeed * .98)
        }
      case YELLOW =>
        val yellowElapsedTime = (System.currentTimeMillis() - yellowStartTime) / 1000.0
        val yellowRemainingTime = getYellowDurationSecs.toDouble - yellowElapsedTime
        var vehicleIdx = sortedVehicles.size
        var found = false
        var vehicle: VehicleSprite = null
        while (!found && vehicleIdx > 0) {
          vehicleIdx -= 1
          vehicle = sortedVehicles(vehicleIdx)
          val distanceToLight = (1.0 - vehicle.getPosition) * edgeLen
          val distAtCurrentSpeed = yellowRemainingTime * vehicle.getSpeed
          val distTillNextGreen = (yellowRemainingTime + 5) * vehicle.getSpeed
          if (distAtCurrentSpeed > distanceToLight && distAtCurrentSpeed < distTillNextGreen) {
            found = true
          }
        }
        if (found) {
          vehicle.brake(yellowRemainingTime * vehicle.getSpeed, deltaTime)
        }
      case GREEN =>
        vehicleClosestToLight.accelerate(0.005)
    }
  }
}
