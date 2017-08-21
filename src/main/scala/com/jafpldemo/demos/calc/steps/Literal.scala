package com.jafpldemo.demos.calc.steps

import com.jafpl.steps.PortBindingSpecification
import com.jafpldemo.DefaultStep

class Literal(val value: Long) extends DefaultStep {
  override def inputSpec: PortBindingSpecification = PortBindingSpecification.NONE
  override def outputSpec: PortBindingSpecification = PortBindingSpecification.RESULT

  override def run(): Unit = {
    consumer.get.send("result", value)
  }
}
