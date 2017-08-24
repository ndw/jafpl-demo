package com.jafpldemo.demos.calc.steps

import com.jafpl.steps.PortSpecification
import com.jafpldemo.DefaultStep

class UnaryOp(op: String) extends DefaultStep {
  var operand: Long = 0

  override def inputSpec: PortSpecification =
    new PortSpecification(Map("operand" -> "1"))
  override def outputSpec: PortSpecification = PortSpecification.RESULT

  override def receive(port: String, item: Any): Unit = {
    val number = item match {
      case num: Long => num
      case _ => throw new RuntimeException("Not a number: " + item)
    }

    port match {
      case "operand" => operand = number
      case _ => throw new RuntimeException("Unexpected port: " + port)
    }
  }

  override def run(): Unit = {
    op match {
      case "+" => consumer.get.send("result", operand)
      case "-" => consumer.get.send("result", -operand)
      case _ => throw new RuntimeException("Unexpected operation: " + op)
    }
  }
}
