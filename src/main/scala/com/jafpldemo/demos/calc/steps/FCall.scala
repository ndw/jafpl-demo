package com.jafpldemo.demos.calc.steps

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

  override def receive(port: String, item: Any): Unit = {
    val number = item match {
      case num: Long => num
      case _ => throw new RuntimeException("Not a number: " + item)
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
      case "max" => consumer.get.send("result", args.max)
      case "min" => consumer.get.send("result", args.min)
      case "sum" => consumer.get.send("result", args.sum)
      case _ => throw new RuntimeException("Unexpected operation: " + name)
    }
  }
}
