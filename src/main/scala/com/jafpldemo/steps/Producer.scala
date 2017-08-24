package com.jafpldemo.steps

import com.jafpl.steps.PortSpecification
import com.jafpldemo.DefaultStep

class Producer(val items: List[String]) extends DefaultStep {
  override def inputSpec = PortSpecification.NONE
  override def outputSpec = PortSpecification.RESULTSEQ

  def this(item: String) {
    this(List(item))
  }

  override def run(): Unit = {
    for (item <- items) {
      consumer.get.send("result", item)
    }
  }
}
