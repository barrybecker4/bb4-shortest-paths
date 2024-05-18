package com.barrybecker4.discreteoptimization.traffic

import com.barrybecker4.discreteoptimization.common.graph.GraphTstUtil
import com.barrybecker4.discreteoptimization.common.graph.visualization.{GraphStreamAdapter, GraphViewerFrame}
import com.barrybecker4.discreteoptimization.traffic.viewer.TrafficViewerFrame


/**
 * Ideas
 * - Each Intersection should have a SignalStrategy
 *    - it can be configurable per node in the traffic config file
 *    - Enum like DumbTrafficLight, SmartTrafficLight, CollisionAvoidance
 *    - The 
 */
object TrafficApp extends App {

  val frame = new TrafficViewerFrame()

}
