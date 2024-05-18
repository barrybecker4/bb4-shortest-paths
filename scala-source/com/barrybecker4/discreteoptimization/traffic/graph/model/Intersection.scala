package com.barrybecker4.discreteoptimization.traffic.graph.model

import com.barrybecker4.discreteoptimization.common.Location


case class Intersection(id: Int, location: Location, ports: IndexedSeq[Port]) 
