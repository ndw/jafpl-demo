package com.jafpldemo.examples.steps

import com.jafpl.exceptions.PipelineException
import com.jafpl.messages.{ItemMessage, Message, Metadata}
import com.jafpl.steps.PortSpecification
import com.jafpldemo.DefaultStep

class Uppercase extends DefaultStep {
  override def inputSpec: PortSpecification = PortSpecification.SOURCE
  override def outputSpec: PortSpecification = PortSpecification.RESULT

  override def receive(port: String, message: Message): Unit = {
    var words = ""

    message match {
      case item: ItemMessage =>
        item.item match {
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

          case _ => throw new PipelineException("UnexpectedType", s"Unexpected item type: $item")
        }
      case _ => throw new RuntimeException("Not an item message: " + message)
    }

    consumer.get.receive("result", new ItemMessage(words, Metadata.STRING))
  }
}
