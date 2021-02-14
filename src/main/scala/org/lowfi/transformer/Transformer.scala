package org.lowfi.transformer

import com.opencsv.{CSVReader, CSVWriter}
import org.lowfi.transformer.domain._

import java.io.{Reader, Writer}

object Transformer {
  def transform(reader: Reader,
                writer: Writer,
                transformations: Map[HeaderName, TransformationType]): Unit = {
    val csvReader = new CSVReader(reader)
    val csvWriter = new CSVWriter(writer)

    val rawHeaders: Array[String] = csvReader.readNextSilently()//.map(_.trim())
    csvWriter.writeNext(rawHeaders) // write out header

    var rawValues: Seq[String] = csvReader.readNext()
    while (rawValues != null) {
      // convert raw values into Seq[ValueType]
      val valueTypes: Seq[Value] = rawHeaders.zip(rawValues).map {
        case (headerName, headerValue) =>
          val valueType = transformations.get(HeaderName(headerName)) match {
            case None => Value.Plain(headerValue)
            case Some(transformationType) =>
              transformationType match {
                case TransformationType.Split  => Value.Split(headerValue.split(',').map(_.trim()))
                case TransformationType.Divide => Value.Divide(BigDecimal(headerValue))
              }
          }
          valueType
      }

      val outputRows = valueTypes.zipWithIndex.foldLeft(Seq(DataRow(valueTypes))) {
        case (rows, (valueType, index)) =>
          valueType match {
            case _: Value.Plain      => rows // no transformation
            case _: Value.Divide     => rows // applied in split value
            case Value.Split(values) =>
              // Create a data row per split value
              val splitRows: Seq[DataRow] = values.flatMap { value =>
                rows.map { row =>
                  DataRow(row.values.updated(index, Value.Plain(value)))
                }
              }

              val dividedRows = splitRows.map { row =>
                val updatedValues = row.values.map {
                  case Value.Divide(value) => Value.Divide(value / values.length)
                  case other => other
                }
                DataRow(updatedValues)
              }

              dividedRows
          }
      }

      outputRows.foreach { row =>
        csvWriter.writeNext(row.toArray)
      }

      rawValues = csvReader.readNext()
    }

    csvReader.close()
    csvWriter.close()
  }
}
