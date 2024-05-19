package com.barrybecker4.discreteoptimization.traffic.signals

import com.barrybecker4.discreteoptimization.traffic.signals.{LightState, TrafficSignal}
import com.barrybecker4.discreteoptimization.traffic.signals.LightState._
import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}
import concurrent.duration.DurationInt
import com.barrybecker4.discreteoptimization.traffic.signals.DumbTrafficLight._


class DumbTrafficLight(numStreets: Int) extends TrafficSignal {
  private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
  private var currentStreet: Int = 0
  private var lightState: LightState = RED

  setInitialState()

  override def getOptimalDistance: Double = 3.0
  override def getFarDistance: Double = 30.0
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
}



object DumbTrafficLight {

  private val GREEN_DURATION_SECS = 5
  private val YELLOW_DURATION_SECS = 2

  def main(args: Array[String]): Unit = {
    val trafficLight = new DumbTrafficLight(3)
    val checkInterval = 1.second

    val executor = Executors.newScheduledThreadPool(1)
    executor.scheduleAtFixedRate(new Runnable {
      def run(): Unit = printLightStates(trafficLight)
    }, 0, checkInterval.toMillis, TimeUnit.MILLISECONDS)

    Thread.sleep(30000)
    executor.shutdown()
    trafficLight.shutdown()
  }

  private def printLightStates(trafficLight: DumbTrafficLight): Unit = {
    val states = Range(0, 3).map(i => trafficLight.getLightState(i))
    println(states)
  }
}