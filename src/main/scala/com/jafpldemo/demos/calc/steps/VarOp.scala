package com.jafpldemo.demos.calc.steps

import com.jafpl.messages.{BindingMessage, ItemMessage, Metadata}
import com.jafpl.steps.PortSpecification
import com.jafpldemo.DefaultStep

class VarOp(op: String) extends DefaultStep {
  override def inputSpec: PortSpecification = PortSpecification.NONE
  override def outputSpec: PortSpecification = PortSpecification.RESULT

  var value: Long = 0

  override def receiveBinding(message: BindingMessage): Unit = {
    if (op == message.name) {
      message.message match {
        case item: ItemMessage =>
          item.item match {
            case lnum: Long => value = lnum
            case lint: Integer => value = lint.toLong
            case _ => throw new RuntimeException("Not a number: " + value)
          }
        case _ => throw new RuntimeException("Not a number: " + value)
      }
    }
  }

  override def run(): Unit = {
    consumer.get.receive("result", new ItemMessage(value, Metadata.BLANK))
  }
}
