package com.barrybecker4.discreteoptimization.traffic.signals

import com.barrybecker4.discreteoptimization.traffic.signals.{SignalState, TrafficSignal}
import com.barrybecker4.discreteoptimization.traffic.signals.SignalState.*

import java.util.concurrent.{Executors, ScheduledFuture, Semaphore, TimeUnit}
import concurrent.duration.DurationInt
import com.barrybecker4.discreteoptimization.traffic.signals.SemaphoreTrafficSignal.*
import com.barrybecker4.discreteoptimization.traffic.vehicles.VehicleSprite
import com.barrybecker4.discreteoptimization.traffic.viewer.TrafficGraphUtil.sleep
import org.graphstream.graph.Node

import scala.annotation.tailrec

/**
 * A more intelligent traffic light system that uses a semaphore to control the traffic lights.
 *  - If a car is within double yellow distance, then try to take semaphore and become green.
 *    Others remain red.
 *  - A light stays green until no cars within double yellow empty or time exceeded.
 *    If time exceeded, then turn yellow, else straight to red.
 *  - The next street (i.e. intersection node port) with cars waiting gets the semaphore and turns green.
 *  - If no cars coming, all will be red, and semaphore up for grabs.
 *
 * @param numStreets the number of streets leading into the intersection
 */
class SemaphoreTrafficSignal(numStreets: Int) extends TrafficSignal(numStreets) {
  private val lightStates: Array[SignalState] = Array.fill(numStreets)(RED)
  private var currentSchedule: ScheduledFuture[?] = _
  private var streetWithSemaphore: Int = AVAILABLE

  override def getGreenDurationSecs: Int = 6
  override def getLightState(street: Int): SignalState = lightStates(street)

  def shutdown(): Unit = scheduler.shutdown()

  def handleTraffic(sortedVehicles: IndexedSeq[VehicleSprite],
                    portId: Int, edgeLen: Double, deltaTime: Double): Unit = {
    handleTrafficBasedOnLightState(sortedVehicles, portId, edgeLen, deltaTime)
    updateSemaphore(sortedVehicles, portId, edgeLen)
    updateStreetState(sortedVehicles)
  }

  private def updateSemaphore(sortedVehicles: IndexedSeq[VehicleSprite],
                              portId: Int, edgeLen: Double): Unit = {
    val lightState = getLightState(portId)
    streetWithSemaphore match {
      case AVAILABLE =>
        assert(lightState == RED, "The light state was unexpectedly " + lightState)
        switchToGreen(portId, sortedVehicles, edgeLen)
      case `portId` =>
        assert(lightState != RED, "The light state was unexpectedly " + lightState)
        if (areCarsComing(sortedVehicles, edgeLen)) {
          // already have it, stay yellow or green
        } else if (currentSchedule != null && lightState == GREEN) {
          // No cars are coming, so give up the semaphore
          println("No cars coming on street " + portId + " so canceling schedule and switching to red")
          currentSchedule.cancel(true)
          switchToRed(portId)
        }
      case _ =>
        // do nothing. Some other street has the semaphore
        //println("different street has the semaphore = " + streetWithSemaphore + " port=" + portId + " lightState=" + lightState)
    }
  }

  private def switchToGreen(street: Int, sortedVehicles: IndexedSeq[VehicleSprite],
                            edgeLen: Double): Unit = {
    assert(lightStates(street) == RED)
    assert(streetWithSemaphore == AVAILABLE, "semaphore was not available. It was " + streetWithSemaphore)
    if (areCarsComing(sortedVehicles, edgeLen)) {
      lightStates(street) = GREEN
      streetWithSemaphore = street
      val vehicleClosestToLight = sortedVehicles.last
      vehicleClosestToLight.accelerate(0.01)
      currentSchedule = scheduler.schedule(new Runnable {
        def run(): Unit = switchToYellow(street, sortedVehicles, edgeLen)
      }, getGreenDurationSecs, TimeUnit.SECONDS)
    }
  }

  private def switchToYellow(street: Int, sortedVehicles: IndexedSeq[VehicleSprite],
                             edgeLen: Double): Unit = {
    assert(lightStates(street) == GREEN)
    lightStates(street) = YELLOW
    yellowStartTime = System.currentTimeMillis()
    assert(streetWithSemaphore == street)
    println("switched to yellow and scheduling switch to red for street " + street + " schedule=" + currentSchedule)
    currentSchedule.cancel(true)
    currentSchedule = scheduler.schedule(new Runnable {
      def run(): Unit = switchToRed(street)
    }, getYellowDurationSecs, TimeUnit.SECONDS)
  }

  private def switchToRed(street: Int): Unit = {
    if (lightStates(street) == YELLOW) {
      println("switching to red from yellow on street " + street)
    }
    lightStates(street) = RED
    assert(streetWithSemaphore == street)
    streetWithSemaphore = AVAILABLE
  }

  private def updateStreetState(sortedVehicles: IndexedSeq[VehicleSprite]): Unit = {
    // check if there are any stopped vehicles withing 1.5 * yellow distance from the start of the street
    // If so, then set state to JAMMED
    
  }
  

  private def areCarsComing(sortedVehicles: IndexedSeq[VehicleSprite], edgeLen: Double): Boolean =
    //sortedVehicles.exists(_.getPosition < getFarDistance / edgeLen)
    sortedVehicles.nonEmpty
}


object SemaphoreTrafficSignal {

  private val AVAILABLE = -1
  def main(args: Array[String]): Unit = {
    val numStreets = 5
    val trafficLight = new SemaphoreTrafficSignal(numStreets)
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