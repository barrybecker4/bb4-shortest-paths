package com.barrybecker4.discreteoptimization.traffic.signals

import com.barrybecker4.discreteoptimization.traffic.signals.{LightState, TrafficSignal}
import com.barrybecker4.discreteoptimization.traffic.signals.LightState.*

import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}
import concurrent.duration.DurationInt
import com.barrybecker4.discreteoptimization.traffic.signals.DumbTrafficLight.*
import com.barrybecker4.discreteoptimization.traffic.vehicles.VehicleSprite
import org.graphstream.graph.Node

/**
 * Only one street is allowed to proceed at once (on green). 
 * That should prevent the possibility of accidents.
 * @param numStreets the number of streets leading into the intersection
 */
class DumbTrafficLight(numStreets: Int) extends TrafficSignal {
  private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
  private var currentStreet: Int = 0
  private var lightState: LightState = RED

  setInitialState()

  override def getLightState(street: Int): LightState = {
    if (street == currentStreet) lightState else RED
  }

  def shutdown(): Unit = scheduler.shutdown()

  /**
   * if the light is red, then first car should already be stopped
   * if the light is yellow,
   *   then all cars closer than speed * yellowTime should continue
   *   then the first car further than speed * yellowTime should prepare to stop
   * if the light is green, then all cars should continue
   * In builder, add disconnected nodes around the intersection node to represent the traffic light.
   *
   * draw the lights at intersection nodes
   */
  def handleTraffic(sortedVehicles: IndexedSeq[VehicleSprite],
                    portId: Int, edgeLen: Double, deltaTime: Double): Unit = {
    val lightState = getLightState(portId)
    val vehicleClosestToLight = sortedVehicles.last

    lightState match {
      case RED =>
        // if the light is red, then first car should already be stopped if it is close to the light
        if (vehicleClosestToLight.getSpeed > 0.0 && vehicleClosestToLight.getPosition > 0.95) {
          println("vehicleClosestToLight.getSpeed=" + vehicleClosestToLight.getSpeed + " should have been 0")
          vehicleClosestToLight.stop()
        }
      case YELLOW =>
        val yellowTime = getYellowDurationSecs.toDouble
        var vehicleIdx = sortedVehicles.size - 1
        var vehicle = vehicleClosestToLight
        while ((1.0 - vehicle.getPosition) * edgeLen < yellowTime * vehicle.getSpeed && vehicleIdx > 0) {
          vehicleIdx -= 1
          vehicle = sortedVehicles(vehicleIdx)
        }
        vehicle.brake(yellowTime * vehicle.getSpeed * 0.9, deltaTime)
      case GREEN =>
        vehicleClosestToLight.accelerate(0.01)
    }
  }

  // Function to initialize the traffic light state and scheduling
  private def setInitialState(): Unit = switchToGreen()

  // Function to switch the light to green
  private def switchToGreen(): Unit = {
    lightState = GREEN
    scheduler.schedule(new Runnable {
      def run(): Unit = switchToYellow()
    }, getGreenDurationSecs, TimeUnit.SECONDS)
  }

  // Function to switch the light to yellow
  private def switchToYellow(): Unit = {
    lightState = YELLOW
    scheduler.schedule(new Runnable {
      def run(): Unit = switchToRed()
    }, getYellowDurationSecs, TimeUnit.SECONDS)
  }

  private def switchToRed(): Unit = {
    lightState = RED
    currentStreet = (currentStreet + 1) % numStreets
    switchToGreen()
  }

  private def printLightStates(): Unit = {
    val states = Range(0, numStreets).map(i => getLightState(i))
    println(states)
  }
}


object DumbTrafficLight {

  def main(args: Array[String]): Unit = {
    val numStreets = 5
    val trafficLight = new DumbTrafficLight(numStreets)
    val checkInterval = 1.second

    val executor = Executors.newScheduledThreadPool(1)
    executor.scheduleAtFixedRate(new Runnable {
      def run(): Unit = trafficLight.printLightStates()
    }, 0, checkInterval.toMillis, TimeUnit.MILLISECONDS)

    Thread.sleep(30000)
    executor.shutdown()
    trafficLight.shutdown()
  }
}
