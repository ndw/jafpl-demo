package com.jafpldemo.examples.steps

import com.jafpl.steps.PortSpecification
import com.jafpldemo.DefaultStep

class Identity() extends DefaultStep {
  override def inputSpec: PortSpecification = PortSpecification.SOURCESEQ
  override def outputSpec: PortSpecification = PortSpecification.RESULTSEQ

  override def receive(port: String, item: Any): Unit = {
    consumer.get.send("result", item)
  }
}
