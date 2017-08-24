package com.jafpldemo.demos.calc.steps

import com.jafpl.steps.PortSpecification
import com.jafpldemo.DefaultStep

class VarOp(op: String) extends DefaultStep {
  override def inputSpec: PortSpecification = PortSpecification.NONE
  override def outputSpec: PortSpecification = PortSpecification.RESULT

  var value: Long = 0

  override def receiveBinding(varname: String, item: Any): Unit = {
    if (op == varname) {
      item match {
        case lnum: Long => value = lnum
        case lint: Integer => value = lint.toLong
        case _ => throw new RuntimeException("Not a number: " + value)
      }
    }
  }

  override def run(): Unit = {
    consumer.get.send("result", value)
  }
}
