package com.barrybecker4.discreteoptimization.traffic.vehicles

import com.barrybecker4.discreteoptimization.traffic.vehicles.placement.VehiclePlacer
import org.graphstream.graph.Graph
import org.graphstream.ui.spriteManager.{Sprite, SpriteManager}


class VehicleSpriteGenerator(private val numSprites: Int) {

  /** The set of sprites. */
  private var spriteManager: SpriteManager = _

  def addSprites(graph: Graph): Unit = {
    spriteManager = new SpriteManager(graph)
    spriteManager.setSpriteFactory(new VehicleSpriteFactory)
    for (i <- 0 until numSprites) {
      spriteManager.addSprite(s"$i")
    }
    new VehiclePlacer(spriteManager, graph).placeVehicleSprites()
  }

  def moveSprites(): Unit = {
    spriteManager.forEach((s: Sprite) => s.asInstanceOf[VehicleSprite].move())
  }
}
