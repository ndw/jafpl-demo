package com.jafpldemo.examples

import com.jafpl.config.Jafpl
import com.jafpl.runtime.GraphRuntime
import com.jafpldemo.config.PrimitiveRuntimeConfiguration
import com.jafpldemo.examples.steps.NOP

object Example1 extends App {
  val graph = Jafpl.newInstance().newGraph()

  val pipeline = graph.addPipeline("pipeline")
  val nop      = pipeline.addAtomic(new NOP(), "nop")

  val config = new PrimitiveRuntimeConfiguration()
  val runtime = new GraphRuntime(graph, config)
  runtime.run()
}
