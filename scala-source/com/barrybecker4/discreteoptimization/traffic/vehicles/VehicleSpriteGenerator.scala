package com.barrybecker4.discreteoptimization.traffic.vehicles

import com.barrybecker4.discreteoptimization.traffic.vehicles.placement.VehiclePlacer
import org.graphstream.graph.Graph
import org.graphstream.ui.spriteManager.{Sprite, SpriteManager}


class VehicleSpriteGenerator(private val numSprites: Int) {

  /** The set of sprites. */
  private var sprites: SpriteManager = _

  def addSprites(graph: Graph): Unit = {
    sprites = new SpriteManager(graph)
    sprites.setSpriteFactory(new VehicleSpriteFactory)
    for (i <- 0 until numSprites) {
      sprites.addSprite(i + "")
    }
    new VehiclePlacer(sprites, graph).placeVehicleSprites()
  }

  def moveSprites(): Unit = {
    sprites.forEach((s: Sprite) => s.asInstanceOf[VehicleSprite].move())
  }
}
