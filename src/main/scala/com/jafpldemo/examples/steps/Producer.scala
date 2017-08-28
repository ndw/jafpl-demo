package com.jafpldemo.examples.steps

import com.jafpl.messages.Metadata
import com.jafpl.steps.PortSpecification
import com.jafpldemo.DefaultStep

class Producer(items: List[String]) extends DefaultStep {
  override def inputSpec: PortSpecification = PortSpecification.NONE
  override def outputSpec: PortSpecification = PortSpecification.RESULTSEQ

  def this(item: String) {
    this(List(item))
  }

  override def run(): Unit = {
    for (item <- items) {
      consumer.get.receive("result", item, Metadata.STRING)
    }
  }
}
