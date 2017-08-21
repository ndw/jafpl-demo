package com.jafpldemo.steps

import com.jafpldemo.DefaultStep

import scala.collection.mutable.ListBuffer

class BufferedConsumer extends DefaultStep {
  val _items: ListBuffer[Any] = ListBuffer.empty[Any]

  def items: List[Any] = _items.toList

  override def receive(port: String, item: Any): Unit = {
    super.receive(port, item)
    _items += item
  }
}
