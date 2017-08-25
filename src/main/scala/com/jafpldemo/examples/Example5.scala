package com.jafpldemo.examples

import com.jafpl.graph.Graph
import com.jafpl.runtime.GraphRuntime
import com.jafpldemo.config.PrimitiveRuntimeConfiguration
import com.jafpldemo.examples.steps.{Consumer, Count, Identity, Producer, Uppercase}

object Example5 extends App {
  val config = new PrimitiveRuntimeConfiguration()
  val graph  = new Graph()

  val pipeline = graph.addPipeline("pipeline")
  val producer = pipeline.addAtomic(new Producer(List("Hello, world")), "producer")
  val upper    = pipeline.addAtomic(new Uppercase(), "uppercase")
  val consumer = pipeline.addAtomic(new Consumer(), "consumer")

  graph.addEdge(producer, "result", upper, "source")
  graph.addEdge(upper, "result", consumer, "source")

  val runtime = new GraphRuntime(graph, config)
  runtime.run()
}
