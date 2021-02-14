package org.lowfi.transformer.domain

case class DataRow(values: Seq[Value]) {
  def toArray: Array[String] = values.toArray.map(_.toString)
}
