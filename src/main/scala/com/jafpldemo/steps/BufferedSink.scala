package com.jafpldemo.steps

import com.jafpl.messages.Metadata
import com.jafpldemo.DefaultStep

import scala.collection.mutable.ListBuffer

class BufferedSink extends DefaultStep {
  val _items: ListBuffer[Any] = ListBuffer.empty[Any]

  def items: List[Any] = _items.toList

  override def receive(port: String, item: Any, metadata: Metadata): Unit = {
    super.receive(port, item, metadata)
    _items += item
  }
}
