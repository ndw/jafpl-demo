package com.jafpldemo.demos.simple

import com.jafpl.graph.Graph
import com.jafpl.runtime.GraphRuntime
import com.jafpldemo.config.PrimitiveRuntimeConfiguration
import com.jafpldemo.examples.steps.Producer
import com.jafpldemo.steps.BufferedSink

object Driver extends App {
  val config = new PrimitiveRuntimeConfiguration()
  val graph = new Graph()

  runGraph()

  def runGraph(): Unit = {
    val bc = new BufferedSink()

    val pipeline = graph.addPipeline("pipeline")
    val producer = pipeline.addAtomic(new Producer("Hello, World"), "producer")
    val consumer = pipeline.addAtomic(bc, "consumer")

    graph.addEdge(producer, "result", pipeline, "result")
    graph.addEdge(pipeline, "result", consumer, "source")

    val runtime = new GraphRuntime(graph, config)
    runtime.run()

    for (item <- bc.items) {
      println(item)
    }
  }
}
