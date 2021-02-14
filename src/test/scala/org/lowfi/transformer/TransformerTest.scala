package org.lowfi.transformer

import org.lowfi.transformer.domain._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.io.{BufferedReader, StringReader, StringWriter}

class TransformerTest extends AnyWordSpec with Matchers {
  "Transformer" should {
    "split and divide CSV" in {
      val input =
        """|ID,"Groups Included",Total
           |1,"A,B",100
           |2,A,50
           |3,"B,C",100""".stripMargin
      val reader = new BufferedReader(new StringReader(input))
      val writer = new StringWriter()
      val transformations = Map(
        HeaderName("Groups Included") -> TransformationType.Split,
        HeaderName("Total") -> TransformationType.Divide
      )
      Transformer.transform(reader, writer, transformations)
      writer.toString shouldBe
        """"ID","Groups Included","Total"
          |"1","A","50"
          |"1","B","50"
          |"2","A","50"
          |"3","B","50"
          |"3","C","50"
          |""".stripMargin
    }
  }
}
