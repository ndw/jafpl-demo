package com.jafpldemo.examples.steps

import com.jafpl.exceptions.StepException
import com.jafpl.steps.PortSpecification
import com.jafpldemo.DefaultStep

class Uppercase extends DefaultStep {
  override def inputSpec = PortSpecification.SOURCE
  override def outputSpec = PortSpecification.RESULT

  override def receive(port: String, item: Any): Unit = {
    var words = ""

    item match {
      case stringItem: String =>
        var s = stringItem
        val nextWord = "(\\W*)(\\w+)(.*)".r
        var more = true
        while (more) {
          s match {
            case nextWord(prefix, word, rest) =>
              if (words != "") {
                words += " "
              }
              words += word.toUpperCase
              s = rest
            case _ =>
              more = false
          }
        }

      case _ => throw new StepException("UnexpectedType", s"Unexpected item type: $item")
    }

    consumer.get.send("result", words)
  }
}
