package com.barrybecker4.graph.visualization.render

enum UiClass(val name: String, val isHighlight: Boolean) {
  case PLAIN extends UiClass("", false)
  case LARGE extends UiClass("large", false)
  case VISITED extends UiClass("visited", true)
  case HIGHLIGHTED extends UiClass("highlighted", true)
}
