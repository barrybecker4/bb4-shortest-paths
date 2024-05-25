package com.barrybecker4.discreteoptimization.traffic.signals

import com.barrybecker4.discreteoptimization.traffic.signals.{LightState, TrafficSignal}
import com.barrybecker4.discreteoptimization.traffic.signals.LightState._
import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}
import concurrent.duration.DurationInt
import com.barrybecker4.discreteoptimization.traffic.signals.DumbTrafficLight._

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

  // Function to initialize the traffic light state and scheduling
  private def setInitialState(): Unit = switchToGreen()

  // Function to switch the light to green
  private def switchToGreen(): Unit = {
    lightState = GREEN
    scheduler.schedule(new Runnable {
      def run(): Unit = switchToYellow()
    }, GREEN_DURATION_SECS, TimeUnit.SECONDS)
  }

  // Function to switch the light to yellow
  private def switchToYellow(): Unit = {
    lightState = YELLOW
    scheduler.schedule(new Runnable {
      def run(): Unit = switchToRed()
    }, YELLOW_DURATION_SECS, TimeUnit.SECONDS)
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

  private val GREEN_DURATION_SECS = 5
  private val YELLOW_DURATION_SECS = 2

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
