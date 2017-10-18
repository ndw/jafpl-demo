package com.jafpldemo.demos.calc.steps

import com.jafpl.messages.{ItemMessage, Message, Metadata}
import com.jafpl.steps.PortSpecification
import com.jafpldemo.DefaultStep

class BinaryOp(op: String) extends DefaultStep {
  var left: Long = 0
  var right: Long = 0

  override def inputSpec: PortSpecification =
    new PortSpecification(Map("left" -> "1", "right" -> "1"))
  override def outputSpec: PortSpecification = PortSpecification.RESULT

  override def receive(port: String, message: Message): Unit = {
    var number: Long = 0

    message match {
      case item: ItemMessage =>
        number = item.item match {
          case num: Long => num
          case _ => throw new RuntimeException("Not a number: " + item.item)
        }
      case _ => throw new RuntimeException("Not an item message: " + message)

    }

    port match {
      case "left" => left = number
      case "right" => right = number
      case _ => throw new RuntimeException("Unexpected port: " + port)
    }
  }

  override def run(): Unit = {
    val message = op match {
      case "+" => new ItemMessage(left + right, Metadata.NUMBER)
      case "-" => new ItemMessage(left - right, Metadata.NUMBER)
      case "*" => new ItemMessage(left * right, Metadata.NUMBER)
      case "/" => new ItemMessage(left / right, Metadata.NUMBER)
      case _ => throw new RuntimeException("Unexpected operation: " + op)
    }

    consumer.get.receive("result", message)
  }
}
