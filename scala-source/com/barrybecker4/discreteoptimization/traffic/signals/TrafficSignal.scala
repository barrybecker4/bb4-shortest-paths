package com.barrybecker4.discreteoptimization.traffic.signals

import com.barrybecker4.discreteoptimization.traffic.signals.LightState


trait TrafficSignal {

  def getOptimalDistance: Double = 3.0
  def getFarDistance: Double = 30.0
  def getLightState(port: Int): LightState

}
