package com.barrybecker4.discreteoptimization.traffic.vehicles

import org.graphstream.ui.graphicGraph.stylesheet.Values
import org.graphstream.ui.spriteManager.Sprite
import org.graphstream.ui.spriteManager.SpriteFactory
import org.graphstream.ui.spriteManager.SpriteManager


class VehicleSpriteFactory extends SpriteFactory {
  override def newSprite(identifier: String, manager: SpriteManager, position: Values) = new VehicleSprite(identifier, manager)
}