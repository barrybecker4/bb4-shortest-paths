package com.barrybecker4.discreteoptimization.traffic.signals

import com.barrybecker4.discreteoptimization.traffic.signals.{LightState, TrafficSignal}
import com.barrybecker4.discreteoptimization.traffic.signals.LightState.*

import java.util.concurrent.{Executors, ScheduledExecutorService, ScheduledFuture, Semaphore, TimeUnit}
import concurrent.duration.DurationInt
import com.barrybecker4.discreteoptimization.traffic.signals.SemaphoreTrafficLight.*
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
class SemaphoreTrafficLight(numStreets: Int) extends TrafficSignal(numStreets) {
  private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
  private var currentSchedule: ScheduledFuture[?] = _
  private var streetWithSemaphore: Int = AVAILABLE
  private val lightStates: Array[LightState] = Array.fill(numStreets)(RED)
  private var yellowStartTime = 0L

  override def getGreenDurationSecs: Int = 6
  override def getLightState(street: Int): LightState = lightStates(street)

  def shutdown(): Unit = scheduler.shutdown()

  def handleTraffic(sortedVehicles: IndexedSeq[VehicleSprite],
                    portId: Int, edgeLen: Double, deltaTime: Double): Unit = {

    handleTrafficBasedOnLightState(sortedVehicles, portId, edgeLen, deltaTime)
    updateSemaphore(sortedVehicles, portId, edgeLen)
  }

  private def handleTrafficBasedOnLightState(sortedVehicles: IndexedSeq[VehicleSprite],
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

  //private def getNextStreet(street: Int) = (street + 1) % numStreets

  private def areCarsComing(sortedVehicles: IndexedSeq[VehicleSprite], edgeLen: Double): Boolean =
    //sortedVehicles.exists(_.getPosition < getFarDistance / edgeLen)
    sortedVehicles.nonEmpty
}


object SemaphoreTrafficLight {

  private val AVAILABLE = -1
  def main(args: Array[String]): Unit = {
    val numStreets = 5
    val trafficLight = new SemaphoreTrafficLight(numStreets)
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