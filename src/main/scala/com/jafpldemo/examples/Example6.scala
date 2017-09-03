package com.jafpldemo.examples

import com.jafpl.graph.Graph
import com.jafpl.runtime.GraphRuntime
import com.jafpldemo.config.PrimitiveRuntimeConfiguration
import com.jafpldemo.examples.steps.{Consumer, Producer, Uppercase}

object Example6 extends App {
  val config = new PrimitiveRuntimeConfiguration()
  val graph  = new Graph()

  val pipeline = graph.addPipeline("pipeline")
  val producer = pipeline.addAtomic(new Producer(List("Hello", "There", "World")), "producer")

  val loop     = pipeline.addForEach("loop")
  val upper    = loop.addAtomic(new Uppercase(), "uppercase")

  val consumer = pipeline.addAtomic(new Consumer(), "consumer")

  graph.addEdge(producer, "result", loop, "source")
  graph.addEdge(loop, "current", upper, "source")
  graph.addEdge(upper, "result", loop, "result")
  graph.addEdge(loop, "result", consumer, "source")

  val runtime = new GraphRuntime(graph, config)
  runtime.run()
}
