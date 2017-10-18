package com.jafpldemo.demos.calc

import java.io.{File, PrintWriter}

import com.jafpl.config.Jafpl
import com.jafpl.graph.{Binding, Node}
import com.jafpl.runtime.GraphRuntime
import com.jafpldemo.config.PrimitiveRuntimeConfiguration
import com.jafpldemo.demos.calc.steps.{BinaryOp, FCall, Literal, UnaryOp, VarOp}
import com.jafpldemo.demos.calc.util.{ExprNode, TreeBuilder}
import com.jafpldemo.io.BufferedConsumer

import scala.collection.mutable

object Driver extends App {
  // N.B. As a matter of convenience, I'm letting these be globals.
  // Bad programmer.

  // Setup our graph object
  private val config = new PrimitiveRuntimeConfiguration()
  private val graph = Jafpl.newInstance().newGraph()

  // Make our pipeline
  private val pipeline = graph.addPipeline()

  // Remember the variable bindings we've made
  private val varbind = mutable.HashMap.empty[String, Binding]

  main()

  private def main(): Unit = {
    val bindings = mutable.HashMap.empty[String, Long]
    var dumpGraph: Option[String] = None
    var expression = ""

    var argpos = 0
    while (argpos < args.length) {
      val arg = args(argpos)
      if (arg.startsWith("-g")) {
        dumpGraph = Some(arg.substring(2))
      } else if (arg.startsWith("-v")) {
        val bind = arg.substring(2)
        val pos = bind.indexOf("=")
        if (pos > 0) {
          bindings.put(bind.substring(0, pos), bind.substring(pos + 1).toLong)
        } else {
          throw new RuntimeException("Unparsable variable binding: " + arg)
        }
      } else {
        expression += " " + arg
      }
      argpos += 1
    }

    expression = expression.trim()

    if (expression == "") {
      println("Usage: [-vname=value] expression")
      System.exit(1)
    }

    println("Evaluate: " + expression)

    // Build a tree from the expression
    val expr = parseExpression(expression)

    // Recursively walk the expression tree, building a pipeline
    val exprpipe = buildPipeline(expr)

    // Use the result of the expression as the result of the pipeline
    graph.addEdge(exprpipe, "result", pipeline, "result")

    // Expose the pipeline output
    graph.addOutput(pipeline, "result")

    // Ok, we're done.
    graph.close()

    // Dump out the graph
    if (dumpGraph.isDefined) {
      if (dumpGraph.get == "") {
        println(graph.asXML)
      } else {
        val pw = new PrintWriter(new File(dumpGraph.get))
        pw.write(graph.asXML.toString)
        pw.close()
      }
    }

    // Spin up a runtime
    val runtime = new GraphRuntime(graph, config)

    for (varname <- runtime.bindings.keySet) {
      if (bindings.contains(varname)) {
        runtime.bindings(varname).set(bindings(varname))
        println(s"Binding : $varname=${bindings(varname)}")
      } else {
        throw new RuntimeException(s"Expression requires $$$varname not provided")
      }
    }

    // Setup a consumer to grab the output
    val bc = new BufferedConsumer()
    runtime.outputs("result").setConsumer(bc)

    // Run it!
    runtime.run()

    // Print the results
    println("Result  : " + bc.items.head)
  }

  def parseExpression(expr: String): ExprNode = {
    val tb = new TreeBuilder()
    val parser = new ExpressionParser(expr, tb)
    parser.parse
    tb.stack.head.simplify()
  }

  def buildPipeline(expr: ExprNode): Node = {
    expr.name match {
      case "AdditiveExpr" =>
        val left = buildPipeline(expr.args.head)
        val right = buildPipeline(expr.args(2))
        val op = expr.args(1).chars.get
        val label = if (op == "-" ) { "subtract" } else { "add" }
        val node = pipeline.addAtomic(new BinaryOp(op), label)
        pipeline.graph.addEdge(left, "result", node, "left")
        pipeline.graph.addEdge(right, "result", node, "right")
        node
      case "MultiplicativeExpr" =>
        val left = buildPipeline(expr.args.head)
        val right = buildPipeline(expr.args(2))
        val op = expr.args(1).chars.get
        val label = if (op == "*" ) { "multiply" } else { "divide" }
        val node = pipeline.addAtomic(new BinaryOp(op), label)
        pipeline.graph.addEdge(left, "result", node, "left")
        pipeline.graph.addEdge(right, "result", node, "right")
        node
      case "FunctionCall" =>
        val node = pipeline.addAtomic(new FCall(expr.chars.get), expr.chars.get)
        var count = 1
        for (arg <- expr.args) {
          val op = buildPipeline(arg)
          pipeline.graph.addEdge(op, "result", node, "arg-" + count)
          count += 1
        }
        node
      case "UnaryExpr" =>
        val operand = buildPipeline(expr.args(1))
        val node = pipeline.addAtomic(new UnaryOp(expr.args.head.chars.get), "UnaryExpr")
        pipeline.graph.addEdge(operand, "result", node, "operand")
        node
      case "Literal" =>
        val node = pipeline.addAtomic(new Literal(expr.chars.get.toLong),
          "number-" + expr.chars.get)
        node
      case "VarRef" =>
        if (!varbind.contains(expr.chars.get)) {
          val binding = graph.addBinding(expr.chars.get)
          varbind.put(expr.chars.get, binding)
        }

        val binding = varbind(expr.chars.get)
        val node = pipeline.addAtomic(new VarOp(expr.chars.get), "Variable")
        graph.addBindingEdge(binding, node)
        node
      case _ => throw new RuntimeException("Unexpected expr name: " + expr.name)
    }
  }
}
