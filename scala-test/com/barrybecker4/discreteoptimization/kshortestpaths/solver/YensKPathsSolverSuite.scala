package com.barrybecker4.discreteoptimization.kshortestpaths.solver

import com.barrybecker4.discreteoptimization.kshortestpaths.solver.BaseSolverSuite
import com.barrybecker4.discreteoptimization.kshortestpaths.solver.{KShortestPathsSolver, GreedyKPathsSolver}

class YensKPathsSolverSuite extends BaseSolverSuite {

  val update = true
  
  test("sp_4_1") {
    verify("sp_4_1", 2, 2, update)
  }

  test("sp_4_2") {
    verify("sp_4_2", 2, 2, update)
  }

  test("sp_5_1") {
    verify("sp_5_1", 3, 2, update)
  }

  test("eclair_5_1") {
    verify("eclair_5_1", 3, 2, update)
  }

  test("eclair_5_2") {
    verify("eclair_5_2", 3, 2, update)
  }

  test("eclair_5_3") {
    verify("eclair_5_3", 3, 2, update)
  }

  test("eclair_6_1") {
    verify("eclair_6_1", 5, 3, update)
  }

  test("eclair_6_2") {
    verify("eclair_6_2", 3, 2, update)
  }

  test("eclair_6_3") {
    verify("eclair_6_3", 3, 2, update)
  }

  test("eclair_6_4") {
    verify("eclair_6_4", 4, 2, update)
  }

  test("eclair_6_5") {
    verify("eclair_6_5", 4, 2, update)
  }

  test("eclair_6_6") {
    verify("eclair_6_6", 4, 2, update)
  }

  test("sp_7_1") {
    verify("sp_7_1", 5, 2, update)
  }

  test("sp_10_1") {
    verify("sp_10_1", 5, 3, update)
  }

  test("sp_50_2") {
    verify("sp_50_2", 5, 3, update)
  }

  test("sp_100_1") {
    verify("sp_100_1", 7, 5, update)
  }

  test("sp_100_2") {
    verify("sp_100_2", 7, 5, update)
  }

  test("sp_100_3") {
    verify("sp_100_3", 10, 7, update)
  }

  test("sp_120") {
    verify("sp_120", 20, 10, update)
  }

  test("sp_400") {
    verify("sp_400", 100, 40, update)
  }

  test("sp_500") {
    verify("sp_500", 200, 50, update)
  }

  // runs in 2.1 s
  test("sp_700") {
    verify("sp_700", 40, 10, update)
  }

  test("test_5") {
    verify("test_5", 2, 2, update)
  }

  test("test_6") {
    verify("test_6", 2, 2, update)
  }

  test("test_6_1") {
    verify("test_6_1", 2, 2, update)
  }

  test("test_6_2") {
    verify("test_6_2", 2, 2, update)
  }

  test("test_7") {
    verify("test_7", 3, 3, update)
  }

  test("test_8") {
    verify("test_8", 3, 3, update)
  }

  test("test_15") {
    verify("test_15", 4, 3, update)
  }

  test("test_50") {
    verify("test_50", 5, 4, update)
  }

  test("test_50_2") {
    verify("test_50_2", 10, 5, update)
  }

  /*
  test("sp_325729_1") {
    verify("sp_325729_1", update)
  }*/

  override def createSolver(): KShortestPathsSolver = GreedyKPathsSolver(2)

  override def solverName(): String = "kpaths"
}
