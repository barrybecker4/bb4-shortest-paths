package com.barrybecker4.discreteoptimization.traffic.signals

import com.barrybecker4.discreteoptimization.traffic.signals.LightState


trait TrafficSignal {

  def getOptimalDistance: Double = 3.0
  def getFarDistance: Double = 30.0
  def getYellowDurationSecs: Int = 3
  def getGreenDurationSecs: Int = 7
  def getLightState(port: Int): LightState

}
