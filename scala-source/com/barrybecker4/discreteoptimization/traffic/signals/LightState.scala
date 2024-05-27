package com.barrybecker4.discreteoptimization.traffic.signals

enum LightState(val color: String) {
  case GREEN extends LightState("#22FF2255;")
  case YELLOW extends LightState("#FFFF2266;")
  case RED extends LightState("#FF222244;")
}