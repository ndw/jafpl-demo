package com.jafpldemo.demos.calc.steps

import com.jafpl.messages.Metadata
import com.jafpl.steps.PortSpecification
import com.jafpldemo.DefaultStep

class BinaryOp(op: String) extends DefaultStep {
  var left: Long = 0
  var right: Long = 0

  override def inputSpec: PortSpecification =
    new PortSpecification(Map("left" -> "1", "right" -> "1"))
  override def outputSpec: PortSpecification = PortSpecification.RESULT

  override def receive(port: String, item: Any, metadata: Metadata): Unit = {
    val number = item match {
      case num: Long => num
      case _ => throw new RuntimeException("Not a number: " + item)
    }

    port match {
      case "left" => left = number
      case "right" => right = number
      case _ => throw new RuntimeException("Unexpected port: " + port)
    }
  }

  override def run(): Unit = {
    op match {
      case "+" => consumer.get.receive("result", left + right, Metadata.NUMBER)
      case "-" => consumer.get.receive("result", left - right, Metadata.NUMBER)
      case "*" => consumer.get.receive("result", left * right, Metadata.NUMBER)
      case "/" => consumer.get.receive("result", left / right, Metadata.NUMBER)
      case _ => throw new RuntimeException("Unexpected operation: " + op)
    }
  }
}
