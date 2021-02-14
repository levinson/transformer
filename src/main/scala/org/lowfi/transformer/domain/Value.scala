package org.lowfi.transformer.domain

sealed trait Value

object Value {

  case class Plain(value: String) extends Value {
    override def toString: String = value
  }

  case class Split(values: Seq[String]) extends Value {
    override def toString: String = values.mkString(", ")
  }

  case class Divide(value: BigDecimal) extends Value {
    override def toString: String = value.toString
  }

}
