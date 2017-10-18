package com.jafpldemo.demos.calc.steps

import com.jafpl.messages.{ItemMessage, Message, Metadata}
import com.jafpl.steps.PortSpecification
import com.jafpldemo.DefaultStep

import scala.collection.mutable

class FCall(name: String) extends DefaultStep {
  private val args = mutable.ListBuffer.empty[Long]

  override def inputSpec: PortSpecification =
    new PortSpecification(Map("*" -> "*"))
  override def outputSpec: PortSpecification = PortSpecification.RESULT

  override def reset(): Unit = {
    args.clear()
  }

  override def receive(port: String, message: Message): Unit = {
    var number: Long = 0

    message match {
      case item: ItemMessage =>
        number = item.item match {
          case num: Long => num
          case _ => throw new RuntimeException("Not a number: " + item)
        }
      case _ => throw new RuntimeException("Not an item message: " + message)
    }

    val patn = "arg-([0-9]+)".r
    port match {
      case patn(pnum) =>
        val pos = pnum.toInt
        while (args.length < pos) {
          args += 0
        }
        args.update(pos - 1, number)
      case _ => throw new RuntimeException("Unexpected port: " + port)
    }
  }

  override def run(): Unit = {
    name match {
      case "max" => consumer.get.receive("result", new ItemMessage(args.max, Metadata.NUMBER))
      case "min" => consumer.get.receive("result", new ItemMessage(args.min, Metadata.NUMBER))
      case "sum" => consumer.get.receive("result", new ItemMessage(args.sum, Metadata.NUMBER))
      case _ => throw new RuntimeException("Unexpected operation: " + name)
    }
  }
}
