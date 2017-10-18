package com.jafpldemo.steps

import com.jafpl.messages.{ItemMessage, Message, Metadata}
import com.jafpldemo.DefaultStep

import scala.collection.mutable.ListBuffer

class BufferedSink extends DefaultStep {
  val _items: ListBuffer[Any] = ListBuffer.empty[Any]

  def items: List[Any] = _items.toList

  override def receive(port: String, msg: Message): Unit = {
    msg match {
      case item: ItemMessage =>
        _items += item.item
      case _ => throw new RuntimeException("Not an item message: " + msg)
    }
  }
}
