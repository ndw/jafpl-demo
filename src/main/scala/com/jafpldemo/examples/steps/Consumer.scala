package com.jafpldemo.examples.steps

import com.jafpl.messages.{ItemMessage, Message, Metadata}
import com.jafpl.steps.PortSpecification
import com.jafpldemo.DefaultStep

import scala.collection.mutable

class Consumer() extends DefaultStep {
  private val items = mutable.ListBuffer.empty[Any]

  override def inputSpec: PortSpecification = PortSpecification.SOURCESEQ
  override def outputSpec: PortSpecification = PortSpecification.NONE

  override def receive(port: String, message: Message): Unit = {
    message match {
      case item: ItemMessage =>
        items += item.item
      case _ => throw new RuntimeException("Not an item message: " + message)
    }
  }

  override def run(): Unit = {
    for (item <- items) {
      println(item)
    }
  }
}
