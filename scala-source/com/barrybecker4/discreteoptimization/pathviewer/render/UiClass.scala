package com.barrybecker4.discreteoptimization.pathviewer.render

enum UiClass(val name: String) {
  case PLAIN extends UiClass("")
  case VISITED extends UiClass("visited")
  case HIGHLIGHTED extends UiClass("highlighted")
}
