package com.jafpldemo.io

import com.jafpl.messages.Metadata
import com.jafpl.steps.DataConsumer

import scala.collection.mutable.ListBuffer

class BufferedConsumer extends DataConsumer {
  val _items: ListBuffer[Any] = ListBuffer.empty[Any]

  def items: List[Any] = _items.toList

  override def receive(port: String, item: Any, metadata: Metadata): Unit = {
    _items += item
  }

}

