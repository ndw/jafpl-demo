package com.jafpldemo.examples

import com.jafpl.config.Jafpl
import com.jafpl.runtime.GraphRuntime
import com.jafpldemo.config.PrimitiveRuntimeConfiguration
import com.jafpldemo.examples.steps.{Consumer, Producer}

object Example3 extends App {
  val config = new PrimitiveRuntimeConfiguration()
  val graph = Jafpl.newInstance().newGraph()

  val pipeline = graph.addPipeline("pipeline")
  val producer = pipeline.addAtomic(new Producer(List("Hello, world")), "producer")
  val consumer = pipeline.addAtomic(new Consumer(), "consumer")

  graph.addEdge(producer, "result", consumer, "source")

  val runtime = new GraphRuntime(graph, config)
  runtime.run()
}
