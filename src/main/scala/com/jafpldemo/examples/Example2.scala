package com.jafpldemo.examples

import com.jafpl.graph.Graph
import com.jafpl.runtime.GraphRuntime
import com.jafpldemo.config.PrimitiveRuntimeConfiguration
import com.jafpldemo.examples.steps.{NOP, Producer}

object Example2 extends App {
  val config = new PrimitiveRuntimeConfiguration()
  val graph  = new Graph()

  val pipeline = graph.addPipeline("pipeline")
  val producer = pipeline.addAtomic(new Producer(List("Hello, world")), "producer")

  val runtime = new GraphRuntime(graph, config)
  runtime.run()
}
