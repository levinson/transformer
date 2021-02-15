package org.lowfi.transformer

import com.opencsv.{CSVReader, CSVWriter}
import com.typesafe.scalalogging.LazyLogging
import org.lowfi.transformer.domain._

import java.io.{Reader, Writer}

object Transformer extends LazyLogging {
  def transform(reader: Reader, writer: Writer, transformations: Map[HeaderName, TransformationType]): Unit = {
    val csvReader = new CSVReader(reader)
    val csvWriter = new CSVWriter(writer)

    val rawHeaders: Array[String] = csvReader.readNextSilently()
    csvWriter.writeNext(rawHeaders) // write out header

    transformations.keys.foreach { headerName =>
      if (!rawHeaders.contains(headerName.value)) {
        logger.warn(s"Transformation defined for '$headerName' but column does not exist.")
      }
    }

    var rawValues: Array[String] = csvReader.readNext()
    while (rawValues != null) {
      val initialRow = DataRow.fromRawValues(rawHeaders, rawValues, transformations)
      val outputRows = initialRow.values.zipWithIndex.foldLeft(Seq(initialRow)) {
        case (rows, (value, index)) =>
          value match {
            case _: Value.Plain      => rows // no transformation
            case _: Value.Divide     => rows // applied in split value
            case Value.Split(values) =>
              // Create a data row per split value
              val splitRows: Seq[DataRow] = values.flatMap { value =>
                rows.map { row =>
                  DataRow(row.values.updated(index, Value.Plain(value)))
                }
              }

              // Apply divisions to split rows
              splitRows.map { row =>
                val updatedValues = row.values.map {
                  case Value.Divide(value) => Value.Divide(value / values.length)
                  case other               => other
                }
                DataRow(updatedValues)
              }
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
