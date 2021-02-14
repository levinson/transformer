package org.lowfi.transformer

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import org.lowfi.transformer.domain._

import java.io.{BufferedReader, BufferedWriter, FileReader, FileWriter}
import scala.jdk.CollectionConverters._

object Main extends LazyLogging {
  def main(args: Array[String]): Unit = {
    logger.info("Loading transformer configuration")
    val conf = ConfigFactory.load().getConfig("org.lowfi.transformer")
    val inputFilePath = conf.getString("inputFilePath")
    val outputFilePath = conf.getString("outputFilePath")

    logger.debug(s"inputFilePath: $inputFilePath")
    logger.debug(s"outputFilePath: $outputFilePath")

    // read transformations into map
    val transformations: Map[HeaderName, TransformationType] = conf
      .getObject("transformations")
      .entrySet()
      .asScala
      .map { entry =>
        (
          HeaderName(entry.getKey),
          TransformationType.withNameInsensitive(entry.getValue.unwrapped().toString)
        )
      }
      .toMap

    logger.debug(s"transformations: $transformations")

    val reader = new BufferedReader(new FileReader(inputFilePath))
    val writer = new BufferedWriter(new FileWriter(outputFilePath))

    logger.info(s"Applying transformations: $transformations")
    Transformer.transform(reader, writer, transformations)

    logger.info("Completed successfully")
  }
}
