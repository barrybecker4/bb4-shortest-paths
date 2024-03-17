package com.barrybecker4.discreteoptimization.common.graph.algorithms.kshortestpaths.eppstein

import com.barrybecker4.discreteoptimization.common.graph.algorithms.kshortestpaths.eppstein.EppsteinHeap
import scala.collection.mutable.{ArrayBuffer, PriorityQueue}
import scala.annotation.tailrec

/**
 * An array representation of a binary heap with additional functions specialized for implementing Eppstein's algorithm.
 */
class EppsteinArrayHeap(var arrayHeap: ArrayBuffer[EppsteinHeap] = ArrayBuffer[EppsteinHeap]()) {

  def getParentIndex(i: Int): Int = (i - 1) / 2

  def add(h: EppsteinHeap): Unit = {
    arrayHeap.addOne(h)
    bubbleUp(arrayHeap.size - 1)
  }

  def addOutroot(h: EppsteinHeap): Unit = {
    var current = arrayHeap.size
    while (current > 0) {
      val parent = getParentIndex(current)
      val newHeap = arrayHeap(parent).clone
      arrayHeap(parent) = newHeap
      current = parent
    }
    arrayHeap.addOne(h)
    bubbleUp(arrayHeap.size - 1)
  }

  @tailrec private def bubbleUp(current: Int): Unit = {
    if (current == 0) return
    val parent = getParentIndex(current)
    if (arrayHeap(current).sidetrackCost >= arrayHeap(parent).sidetrackCost) return
    val temp = arrayHeap(current)
    arrayHeap(current) = arrayHeap(parent)
    arrayHeap(parent) = temp
    bubbleUp(parent)
  }

  /** Convert from an array representation of a binary heap to a pointer representation of a binary heap, which can fit
   *  consistently within an overall N-ary heap
   */
  def toEppsteinHeap: EppsteinHeap = {
    if (arrayHeap.size eq 0) return null
    val eh = arrayHeap(0)
    var i = 1
    while (i < arrayHeap.size) {
      val h = arrayHeap(i)
      arrayHeap(getParentIndex(i)).addChild(h)

      i += 1
    }
    eh
  }

  /** Convert from an array representation of a binary heap to a pointer representation of a binary heap, which can fit
   *  consistently within an overall non-binary heap.
   */
  def toEppsteinHeap2: EppsteinHeap = {
    var current = arrayHeap.size - 1
    if (current == -1) return null
    while (current >= 0) {
      val childHeap = arrayHeap(current)
      while (childHeap.children.size > childHeap.numOtherSidetracks) childHeap.children.remove(childHeap.children.size - 1)
      val child1 = current * 2 + 1
      val child2 = current * 2 + 2
      if (child1 < arrayHeap.size) arrayHeap(current).addChild(arrayHeap(child1))
      if (child2 < arrayHeap.size) arrayHeap(current).addChild(arrayHeap(child2))
      if (current > 0) current = getParentIndex(current)
      else current = -1
    }
    arrayHeap(0)
  }

  override def clone: EppsteinArrayHeap = {
    val clonedArrayHeap = new EppsteinArrayHeap
    //import scala.collection.JavaConversions._
    for (heap <- arrayHeap) {
      clonedArrayHeap.add(heap)
    }
    clonedArrayHeap
  }
}