package com.barrybecker4.discreteoptimization.common.graph

import java.io.File
import scala.io.Source


trait Parser[M] {

  def parse(fileName: String): M =
    parse(Source.fromFile(fileName), fileName)

  def parse(file: File, problemName: String): M =
    parse(Source.fromFile(file), problemName)

  def parse(source: Source, name: String): M = {
    val problem = parse(source.getLines().toIndexedSeq, name: String)
    source.close()
    problem
  }

  protected def parse(lines: IndexedSeq[String], problemName: String): M
}
