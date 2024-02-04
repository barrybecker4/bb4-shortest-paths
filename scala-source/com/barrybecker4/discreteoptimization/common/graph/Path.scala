package com.barrybecker4.discreteoptimization.common.graph

case class Path(weight: Double, nodes: List[Int]) {

  private def this(parts: Array[String]) =
    this(parts(0).toDouble, parts.drop(1).map(_.toInt).toList)
    
  /** @param line assumed to be in the form "<weight> <node1> <node2> ..."
   */
  def this(line: String) = this(line.split("\\s+"))
  
  override def toString: String = 
    s"$weight ${nodes.mkString(" ")}"
}
