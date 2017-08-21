package com.jafpldemo.demos.calc.steps

import com.jafpl.steps.PortBindingSpecification
import com.jafpldemo.DefaultStep

class BinaryOp(op: String) extends DefaultStep {
  var left: Long = 0
  var right: Long = 0

  override def inputSpec: PortBindingSpecification =
    new PortBindingSpecification(Map("left" -> "1", "right" -> "1"))
  override def outputSpec: PortBindingSpecification = PortBindingSpecification.RESULT

  override def receive(port: String, item: Any): Unit = {
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
      case "+" => consumer.get.send("result", left + right)
      case "-" => consumer.get.send("result", left - right)
      case "*" => consumer.get.send("result", left * right)
      case "/" => consumer.get.send("result", left / right)
      case _ => throw new RuntimeException("Unexpected operation: " + op)
    }
  }
}
