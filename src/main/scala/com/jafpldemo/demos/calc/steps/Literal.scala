package com.jafpldemo.demos.calc.steps

import com.jafpl.messages.{ItemMessage, Metadata}
import com.jafpl.steps.PortSpecification
import com.jafpldemo.DefaultStep

class Literal(val value: Long) extends DefaultStep {
  override def inputSpec: PortSpecification = PortSpecification.NONE
  override def outputSpec: PortSpecification = PortSpecification.RESULT

  override def run(): Unit = {
    consumer.get.receive("result", new ItemMessage(value, Metadata.NUMBER))
  }
}
