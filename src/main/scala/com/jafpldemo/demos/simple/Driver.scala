package com.jafpldemo.demos.simple

import com.jafpl.graph.Graph
import com.jafpl.runtime.GraphRuntime
import com.jafpldemo.config.PrimitiveRuntimeConfiguration
import com.jafpldemo.steps.{BufferedConsumer, Producer}

object Driver extends App {
  val config = new PrimitiveRuntimeConfiguration()
  val graph = new Graph()

  runGraph()

  def runGraph(): Unit = {
    val bc = new BufferedConsumer()

    val pipeline = graph.addPipeline("pipeline")
    val producer = pipeline.addAtomic(new Producer("Hello, World"), "producer")
    val consumer = graph.addAtomic(bc, "consumer")

    graph.addEdge(producer, "result", pipeline.end, "result")
    graph.addEdge(pipeline, "result", consumer, "source")

    val runtime = new GraphRuntime(graph, config)
    runtime.run()

    for (item <- bc.items) {
      println(item)
    }
  }
}
