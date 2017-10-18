package com.jafpldemo.examples

import com.jafpl.config.Jafpl
import com.jafpl.runtime.GraphRuntime
import com.jafpldemo.config.PrimitiveRuntimeConfiguration
import com.jafpldemo.examples.steps.Producer

object Example2 extends App {
  val config = new PrimitiveRuntimeConfiguration()
  val graph = Jafpl.newInstance().newGraph()

  val pipeline = graph.addPipeline("pipeline")
  val producer = pipeline.addAtomic(new Producer(List("Hello, world")), "producer")

  val runtime = new GraphRuntime(graph, config)
  runtime.run()
}
