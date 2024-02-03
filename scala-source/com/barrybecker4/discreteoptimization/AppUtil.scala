package com.barrybecker4.discreteoptimization

object AppUtil {

  def getFileName(args: Array[String]): String = {
    var fileName: String = null
    // get the temp file name
    for (arg <- args) {
      if (arg.startsWith("-file=")) {
        fileName = arg.substring(6)
      }
    }
    if (fileName == null) {
      throw new IllegalArgumentException("No filename provided!")
    }
    fileName
  }
}
