package org.lowfi.transformer.domain

case class HeaderName(value: String) extends AnyVal {
  override def toString: String = value
}
