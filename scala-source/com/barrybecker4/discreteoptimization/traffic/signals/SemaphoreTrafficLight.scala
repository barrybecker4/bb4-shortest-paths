package com.barrybecker4.discreteoptimization.traffic.signals

import com.barrybecker4.discreteoptimization.traffic.signals.{LightState, TrafficSignal}
import com.barrybecker4.discreteoptimization.traffic.signals.LightState._

import java.util.concurrent.{Executors, ScheduledExecutorService, Semaphore, TimeUnit}
import concurrent.duration.DurationInt
import com.barrybecker4.discreteoptimization.traffic.signals.SemaphoreTrafficLight._
import com.barrybecker4.discreteoptimization.traffic.vehicles.VehicleSprite
import org.graphstream.graph.Node

/**
 * A more intelligent traffic light system that uses a semaphore to control the traffic lights.
 * @param numStreets the number of streets leading into the intersection
 */
class SemaphoreTrafficLight(numStreets: Int) extends TrafficSignal {
  private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
  private val semaphore: Semaphore = new Semaphore(1)
  private var currentStreet: Int = 0
  private var lightStates: Array[LightState] = Array.fill(numStreets)(GREEN)
  private var yellowStartTime = 0L
  private val maxGreenTime = 30.seconds

  setInitialState()

  override def getLightState(street: Int): LightState = lightStates(street)

  def shutdown(): Unit = scheduler.shutdown()

  def handleTraffic(sortedVehicles: IndexedSeq[VehicleSprite],
                    portId: Int, edgeLen: Double, deltaTime: Double): Unit = {
    val lightState = getLightState(portId)
    val vehicleClosestToLight = sortedVehicles.lastOption

    lightState match {
      case RED =>
        vehicleClosestToLight.foreach { vehicle =>
          if (vehicle.getSpeed > 0.0 && vehicle.getPosition > 0.96) {
            vehicle.stop()
          } else if (vehicle.getPosition > 0.85) {
            vehicle.setSpeed(vehicle.getSpeed * .98)
          }
        }
      case YELLOW =>
        val yellowElapsedTime = (System.currentTimeMillis() - yellowStartTime) / 1000.0
        val yellowRemainingTime = getYellowDurationSecs.toDouble - yellowElapsedTime
        sortedVehicles.findLast { vehicle =>
          val distanceToLight = (1.0 - vehicle.getPosition) * edgeLen
          val distAtCurrentSpeed = yellowRemainingTime * vehicle.getSpeed
          distAtCurrentSpeed > distanceToLight
        }.foreach { vehicle =>
          vehicle.brake(yellowRemainingTime * vehicle.getSpeed, deltaTime)
        }
      case GREEN =>
        vehicleClosestToLight.foreach(_.accelerate(0.01))
    }
  }

  private def setInitialState(): Unit = switchToGreen(currentStreet)

  private def switchToGreen(street: Int): Unit = {
    if (semaphore.tryAcquire()) {
      lightStates = Array.fill(numStreets)(RED)
      lightStates(street) = GREEN
      scheduler.schedule(new Runnable {
        def run(): Unit = switchToYellow(street)
      }, getGreenDurationSecs, TimeUnit.SECONDS)
    } else {
      lightStates(street) = YELLOW
      yellowStartTime = System.currentTimeMillis()
      scheduler.schedule(new Runnable {
        def run(): Unit = switchToRed(street)
      }, getYellowDurationSecs, TimeUnit.SECONDS)
    }
  }

  private def switchToYellow(street: Int): Unit = {
    lightStates(street) = YELLOW
    yellowStartTime = System.currentTimeMillis()
    scheduler.schedule(new Runnable {
      def run(): Unit = switchToRed(street)
    }, getYellowDurationSecs, TimeUnit.SECONDS)
  }

  private def switchToRed(street: Int): Unit = {
    lightStates(street) = RED
    semaphore.release()
    val nextStreet = (currentStreet + 1) % numStreets
    currentStreet = nextStreet
    switchToGreen(nextStreet)
  }

  private def printLightStates(): Unit = {
    val states = Range(0, numStreets).map(i => getLightState(i))
    println(states)
  }
}


object SemaphoreTrafficLight {

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