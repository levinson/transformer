package org.lowfi.transformer

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import org.lowfi.transformer.domain._

import java.io.{BufferedReader, BufferedWriter, FileReader, FileWriter}
import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success, Try}

object Main extends LazyLogging {

  def main(args: Array[String]): Unit = {
    if (args.length != 2) {
      logger.error("Must define input and output file")
      printUsageAndExit()
    }

    val inputFilePath = args(0)
    val outputFilePath = args(1)
    logger.debug(s"inputFilePath: $inputFilePath")
    logger.debug(s"outputFilePath: $outputFilePath")

    val transformations = ConfigFactory
      .load()
      .getConfig("org.lowfi.transformer")
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

    if (transformations.isEmpty) {
      logger.error("No transformations defined in the configuration!")
      printUsageAndExit()
    }

    logger.debug(s"transformations: $transformations")

    Try {
      val reader = new BufferedReader(new FileReader(inputFilePath))
      val writer = new BufferedWriter(new FileWriter(outputFilePath))

      logger.info(s"Applying transformations: $transformations")
      Transformer.transform(reader, writer, transformations)
    } match {
      case Failure(ex) =>
        logger.error("Failed to apply transformation", ex)
        printUsageAndExit()
      case Success(_) => logger.info("Completed successfully")
    }
  }

  private def printUsageAndExit(): Unit = {
    System.err.println("Usage: bin/transformer -Dconfig.file=<config-file> <input-file> <output-file>")
    System.exit(1)
  }
}
