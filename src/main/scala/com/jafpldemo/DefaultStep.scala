package com.jafpldemo

import com.jafpl.graph.Location
import com.jafpl.messages.{BindingMessage, ItemMessage, Message, Metadata}
import com.jafpl.runtime.RuntimeConfiguration
import com.jafpl.steps.{BindingSpecification, DataConsumer, PortSpecification, Step}

class DefaultStep extends Step {
  protected var location = Option.empty[Location]
  protected var _id = "FIXME:"

  override def id: String = _id

  override def inputSpec: PortSpecification = PortSpecification.ANY
  override def outputSpec: PortSpecification = PortSpecification.ANY
  override def bindingSpec: BindingSpecification = BindingSpecification.ANY

  override def receiveBinding(message: BindingMessage): Unit = {
    // nop
  }

  protected var consumer: Option[DataConsumer] = None

  override def setConsumer(consumer: DataConsumer): Unit = {
    this.consumer = Some(consumer)
  }

  override def setLocation(location: Location): Unit = {
    this.location = Some(location)
  }

  override def receive(port: String, message: Message): Unit = {
    // nop
  }

  override def initialize(config: RuntimeConfiguration): Unit = {
    // nop
  }

  override def run(): Unit = {
    // nop
  }

  override def reset(): Unit = {
    // nop
  }

  override def abort(): Unit = {
    // nop
  }

  override def stop(): Unit = {
    // nop
  }
}
