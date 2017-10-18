package com.jafpldemo.examples

import com.jafpl.config.Jafpl
import com.jafpl.runtime.GraphRuntime
import com.jafpldemo.config.PrimitiveRuntimeConfiguration
import com.jafpldemo.examples.steps.{Consumer, Producer, Uppercase}
import com.jafpldemo.examples.util.StringComposer

object Example7 extends App {
  val config = new PrimitiveRuntimeConfiguration()
  val graph = Jafpl.newInstance().newGraph()

  val pipeline = graph.addPipeline()

  val producer = pipeline.addAtomic(new Producer(List("Hello there, world.")), "producer")
  val viewport = pipeline.addViewport(new StringComposer(), "viewport")
  val upper    = viewport.addAtomic(new Uppercase(), "uppercase")
  val consumer = pipeline.addAtomic(new Consumer(), "consumer")

  graph.addEdge(producer, "result", viewport, "source")
  graph.addEdge(viewport, "current", upper, "source")
  graph.addEdge(upper, "result", viewport, "result")
  graph.addEdge(viewport, "result", pipeline, "result")
  graph.addEdge(pipeline, "result", consumer, "source")

  val runtime = new GraphRuntime(graph, config)
  runtime.run()
}
