package com.barrybecker4.discreteoptimization.pathviewer.render

import java.awt.Color

object PathColors {

  // Can't use const, or IDE does not show the color
  //private val OPACITY = 150

  private val COLORS: Array[Color] = Array(
    new Color(135, 25, 25, 150),
    new Color(170, 160, 0, 150),
    new Color(140, 180, 30, 150),
    new Color(65, 155, 145, 150),
    new Color(65, 125, 195, 150),
    new Color(70, 90, 210, 150),
    new Color(110, 90, 220, 150),
    new Color(130, 70, 160, 150),
    new Color(180, 100, 140, 150),
    new Color(140, 90, 90, 150),
    new Color(100, 120, 10, 150),
    new Color(180, 120, 20, 150),
    new Color(210, 120, 0, 150),
    new Color(120, 50, 100, 150),
    new Color(80, 70, 190, 150),
    new Color(10, 50, 250, 150),
    new Color(10, 100, 200, 150),
    new Color(20, 160, 170, 150),
    new Color(50, 160, 70, 150),
    new Color(90, 140, 10, 150),
  )

  def getColor(idx: Int): Color = {
    COLORS(idx % COLORS.length)
  }

  def getCssColor(idx: Int): String = {
    colorToCss(getColor(idx))
  }

  def colorToCss(color: Color): String =
    String.format("#%02x%02x%02x", color.getRed, color.getGreen, color.getBlue)

}
