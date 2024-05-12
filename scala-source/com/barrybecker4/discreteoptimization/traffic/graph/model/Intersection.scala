package com.barrybecker4.discreteoptimization.traffic.graph.model

import com.barrybecker4.discreteoptimization.common.Location


case class Intersection(location: Location, ports: IndexedSeq[Port]) 
