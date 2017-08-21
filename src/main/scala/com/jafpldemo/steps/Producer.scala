package com.jafpldemo.steps

import com.jafpl.steps.PortBindingSpecification
import com.jafpldemo.DefaultStep

class Producer(val items: List[String]) extends DefaultStep {
  override def inputSpec = PortBindingSpecification.NONE
  override def outputSpec = PortBindingSpecification.RESULTSEQ

  def this(item: String) {
    this(List(item))
  }

  override def run(): Unit = {
    for (item <- items) {
      consumer.get.send("result", item)
    }
  }
}
