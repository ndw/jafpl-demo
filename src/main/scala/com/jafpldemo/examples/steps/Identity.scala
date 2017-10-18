package com.jafpldemo.examples.steps

import com.jafpl.messages.{Message, Metadata}
import com.jafpl.steps.PortSpecification
import com.jafpldemo.DefaultStep

class Identity() extends DefaultStep {
  override def inputSpec: PortSpecification = PortSpecification.SOURCESEQ
  override def outputSpec: PortSpecification = PortSpecification.RESULTSEQ

  override def receive(port: String, msg: Message): Unit = {
    consumer.get.receive("result", msg)
  }
}
