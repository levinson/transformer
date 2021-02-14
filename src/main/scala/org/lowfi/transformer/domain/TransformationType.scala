package org.lowfi.transformer.domain

import enumeratum._

sealed trait TransformationType extends EnumEntry

// enumeration of supported transformations
object TransformationType extends Enum[TransformationType] {
  val values = findValues

  case object Split extends TransformationType
  case object Divide extends TransformationType
}
