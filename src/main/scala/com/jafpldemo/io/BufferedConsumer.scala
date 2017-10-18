package com.jafpldemo.io

import com.jafpl.messages.{ItemMessage, Message, Metadata}
import com.jafpl.steps.DataConsumer

import scala.collection.mutable.ListBuffer

class BufferedConsumer extends DataConsumer {
  val _items: ListBuffer[Any] = ListBuffer.empty[Any]

  override def id: String = this.toString // Hack!

  def items: List[Any] = _items.toList

  override def receive(port: String, message: Message): Unit = {
    message match {
      case item: ItemMessage =>
        _items += item.item
      case _ => throw new RuntimeException("Not an item message: " + message)
    }
  }
}

