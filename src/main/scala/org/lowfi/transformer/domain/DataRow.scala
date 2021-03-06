package org.lowfi.transformer.domain

case class DataRow(values: Seq[Value]) {
  def toArray: Array[String] = values.toArray.map(_.toString)
}

object DataRow {
  def fromRawValues(rawHeaders: Seq[String],
                    rawValues: Seq[String],
                    transformations: Map[HeaderName, TransformationType]): DataRow = {
    val values: Seq[Value] = rawHeaders.zip(rawValues).map {
      case (headerName, headerValue) =>
        transformations.get(HeaderName(headerName)) match {
          case None => Value.Plain(headerValue)
          case Some(transformationType) =>
            transformationType match {
              case TransformationType.Split  => Value.Split(headerValue.split(',').map(_.trim()))
              case TransformationType.Divide => Value.Divide(BigDecimal(headerValue))
            }
        }
    }
    DataRow(values)
  }
}
