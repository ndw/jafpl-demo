package com.jafpldemo.examples

import com.jafpl.graph.Graph
import com.jafpl.runtime.GraphRuntime
import com.jafpldemo.config.PrimitiveRuntimeConfiguration
import com.jafpldemo.examples.steps.{Consumer, Identity, Producer}

object Example4 extends App {
  val config = new PrimitiveRuntimeConfiguration()
  val graph  = new Graph()

  val pipeline = graph.addPipeline("pipeline")
  val producer = pipeline.addAtomic(new Producer(List("Hello, world")), "producer")
  val a        = pipeline.addAtomic(new Identity(), "a")
  val b        = pipeline.addAtomic(new Identity(), "b")
  val consumer = pipeline.addAtomic(new Consumer(), "consumer")

  graph.addEdge(producer, "result", b, "source")
  graph.addEdge(b, "result", a, "source")
  graph.addEdge(a, "result", consumer, "source")

  val runtime = new GraphRuntime(graph, config)
  runtime.run()
}
