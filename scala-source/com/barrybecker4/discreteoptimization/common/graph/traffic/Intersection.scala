package com.barrybecker4.discreteoptimization.common.graph.traffic

import com.barrybecker4.discreteoptimization.common.Location
import com.barrybecker4.discreteoptimization.common.graph.traffic.Port


case class Intersection(location: Location, ports: IndexedSeq[Port]) 
