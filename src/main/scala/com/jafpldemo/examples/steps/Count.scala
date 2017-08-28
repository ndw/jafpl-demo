package com.jafpldemo.examples.steps

import com.jafpl.messages.Metadata
import com.jafpl.steps.PortSpecification
import com.jafpldemo.DefaultStep

class Count() extends DefaultStep {
  private var count: Long = 0

  override def inputSpec: PortSpecification = PortSpecification.SOURCESEQ
  override def outputSpec: PortSpecification = PortSpecification.RESULTSEQ

  override def receive(port: String, item: Any, metadata: Metadata): Unit = {
    count += 1
  }

  override def reset(): Unit = {
    count = 0
  }

  override def run(): Unit = {
    consumer.get.receive("result", count, Metadata.NUMBER)
  }
}
