package com.jafpldemo.demos.calc

import java.io.{File, OutputStreamWriter, PrintWriter, Writer}

import com.jafpl.graph.{ContainerStart, Graph, Node}
import com.jafpl.runtime.GraphRuntime
import com.jafpldemo.config.PrimitiveRuntimeConfiguration
import com.jafpldemo.demos.calc.ExpressionParser.EventHandler
import com.jafpldemo.demos.calc.steps.{BinaryOp, FCall, Literal, UnaryOp}
import com.jafpldemo.steps.BufferedConsumer

import scala.collection.mutable

object Driver extends App {
  val tb = new TreeBuilder()
  val parser = new ExpressionParser("sum((1+2),(3+4+5),9*4) * 4 / 2", tb)
  parser.parse
  val expr = tb.stack.head.simplify()
  //expr.dump()

  val config = new PrimitiveRuntimeConfiguration()
  val graph = new Graph()
  val pipeline = graph.addPipeline()
  val exprnode = buildPipeline(pipeline, expr)
  val bc = new BufferedConsumer()
  val consumer = graph.addAtomic(bc)
  graph.addEdge(exprnode, "result", pipeline.end, "result")
  graph.addEdge(pipeline, "result", consumer, "source")

  val pw = new PrintWriter(new File("/projects/github/xproc/jafpl/pg.xml"))
  pw.write(graph.asXML.toString)
  pw.close

  val runtime = new GraphRuntime(graph, config)
  runtime.run()

  println(bc.items.head)


  def buildPipeline(pipeline: ContainerStart, expr: ExprNode): Node = {
    expr.name match {
      case "AdditiveExpr" =>
        val left = buildPipeline(pipeline, expr.args.head)
        val right = buildPipeline(pipeline, expr.args(2))
        val op = expr.args(1).chars.get
        val label = if (op == "-" ) { "subtract" } else { "add" }
        val node = pipeline.addAtomic(new BinaryOp(op), label)
        pipeline.graph.addEdge(left, "result", node, "left")
        pipeline.graph.addEdge(right, "result", node, "right")
        node
      case "MultiplicativeExpr" =>
        val left = buildPipeline(pipeline, expr.args.head)
        val right = buildPipeline(pipeline, expr.args(2))
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
          val op = buildPipeline(pipeline, arg)
          pipeline.graph.addEdge(op, "result", node, "arg-" + count)
          count += 1
        }
        node
      case "UnaryExpr" =>
        val operand = buildPipeline(pipeline, expr.args(1))
        val node = pipeline.addAtomic(new UnaryOp(expr.args.head.chars.get), "UnaryExpr")
        pipeline.graph.addEdge(operand, "result", node, "operand")
        node
      case "Literal" =>
        val node = pipeline.addAtomic(new Literal(expr.chars.get.toLong),
          "number-" + expr.chars.get)
        node
      case "VarRef" =>
        val node = pipeline.addBinding(expr.chars.get, "???")
        node
      case _ => throw new RuntimeException("Unexpected expr name: " + expr.name)
    }
  }
}

class ExprNode(val name: String, val chars: Option[String]) {
  val args: mutable.ListBuffer[ExprNode] = mutable.ListBuffer.empty[ExprNode]
  var simplified: Boolean = false

  def this(name: String) {
    this(name, None)
  }

  def dump(): Unit = {
    dump("")
  }

  private def dump(indent: String): Unit = {
    if (chars.isDefined) {
      if (name == "TOKEN") {
        println(indent + chars.get)
      } else {
        println(indent + name + ": " + chars.get)
      }
    } else {
      println(indent + name + " (" + args.length + ")")
    }
    for (arg <- args) {
      arg.dump(indent + "  ")
    }
  }

  def simplify(): ExprNode = {
    // N.B. Should only be called on the root note
    //
    // Also: this is a terrible hack. The grammar returns non-binary
    // additive and multiplicative expressions, but that doesn't make
    // for a simple or interesting pipeline. So we turn them all into
    // binary operations. Except that's a fair bit of tree manipulation
    // and "simplified" is a hack.

    var changed: Boolean = true
    var node = this
    while (changed) {
      node = node.simplifyTree().get
      changed = node.changedTree()
    }
    node
  }

  private def simplifyTree(): Option[ExprNode] = {
    val simple = mutable.ListBuffer.empty[ExprNode]
    for (arg <- args) {
      val sarg = arg.simplifyTree()
      if (sarg.isDefined) {
        simple += sarg.get
      } else {
        simplified = true
      }
      args.clear()
      for (arg <- simple) {
        args += arg
      }
    }

    name match {
      case "Expression" =>
        args.head.simplified = true
        Some(args.head)
      case "ValueExpr" =>
        if (args.length == 1) {
          args.head.simplified = true
          Some(args.head)
        } else {
          Some(this)
        }
      case "AdditiveExpr" =>
        if (args.length == 1) {
          args.head.simplified = true
          Some(args.head)
        } else {
          if (args.length > 3) {
            simplified = true
            val expr = new ExprNode("AdditiveExpr")
            expr.args += args.head
            expr.args += args(1)
            expr.args += args(2)
            args.remove(0, 3)
            args.insert(0, expr)
          }
          Some(this)
        }
      case "MultiplicativeExpr" =>
        if (args.length == 1) {
          args.head.simplified = true
          Some(args.head)
        } else {
          if (args.length > 3) {
            simplified = true
            val expr = new ExprNode("MultiplicativeExpr")
            expr.args += args.head
            expr.args += args(1)
            expr.args += args(2)
            args.remove(0, 3)
            args.insert(0, expr)
          }
          Some(this)
        }
      case "UnaryExpr" =>
        if (args.length == 1) {
          args.head.simplified = true
          Some(args.head)
        } else {
          if (args.length == 2
            && args.head.name == "TOKEN" && args.head.chars.get == "+") {
            args(1).simplified = true
            Some(args(1))
          } else {
            Some(this)
          }
        }
      case "ParenthesizedExpr" =>
        args(1).simplified = true
        Some(args(1))
      case "FunctionCall" =>
        if (chars.isEmpty) {
          val node = new ExprNode(name, args.head.chars)
          var pos = 2
          while (pos < args.length) {
            node.args += args(pos)
            pos += 2
          }
          node.simplified = true
          Some(node)
        } else {
          Some(this)
        }
      case "VarRef" =>
        if (chars.isEmpty) {
          val node = new ExprNode(name, args(1).chars)
          node.simplified = true
          Some(node)
        } else {
          Some(this)
        }

      case "EOF" =>
        simplified = true
        None
      case _ =>
        Some(this)
    }
  }

  private def changedTree(): Boolean = {
    var changed = simplified
    simplified = false
    for (arg <- args) {
      changed = changed || arg.changedTree()
    }
    changed
  }
}

class TreeBuilder() extends EventHandler {
  private var input: String = null

  val stack: mutable.ListBuffer[ExprNode] = mutable.ListBuffer.empty[ExprNode]

  def reset(string: String) {
    input = string
  }

  def startNonterminal(name: String, begin: Int) {
    stack += new ExprNode(name)
  }

  def endNonterminal(name: String, end: Int) {
    val popped = mutable.ListBuffer.empty[ExprNode]
    while (stack.last.name != name) {
      popped.insert(0, stack.last)
      stack.remove(stack.size - 1)
    }
    for (arg <- popped) {
      stack.last.args += arg
    }
  }

  def terminal(name: String, begin: Int, end: Int) {
    val tag = if (name(0) == '\'') "TOKEN" else name

    stack += new ExprNode(tag, Some(characters(begin,end)))
  }

  def whitespace(begin: Int, end: Int) {
    // nop
  }

  private def characters(begin: Int, end: Int): String = {
    if (begin < end) {
      input.substring(begin, end)
    } else {
      ""
    }
  }
}

class SaveTreeBuilder() extends EventHandler {
  private var delayedTag: String = null
  private var input: String = null
  private val stack = mutable.ListBuffer.empty[ExprNode]

  def reset(string: String) {
    input = string
  }

  def startNonterminal(name: String, begin: Int) {
    println("snt: " + name)
    if (delayedTag != null) {
      println("opening: " + delayedTag)
      stack += new ExprNode(delayedTag)
    }
    delayedTag = name
  }

  def endNonterminal(name: String, end: Int) {
    println("closing: " + delayedTag)
    val popped = mutable.ListBuffer.empty[ExprNode]
    while (stack.last.name != delayedTag) {
      popped += stack.last
      stack.remove(stack.size - 1)
    }
    for (arg <- popped) {
      stack.last.args += arg
    }
  }

  def terminal(name: String, begin: Int, end: Int) {
    val tag = if (name(0) == '\'') "TOKEN" else name

    stack += new ExprNode(tag, Some(characters(begin,end)))
  }

  def whitespace(begin: Int, end: Int) {
    // nop
  }

  private def characters(begin: Int, end: Int): String = {
    if (begin < end) {
      input.substring(begin, end)
    } else {
      ""
    }
  }
}

class XmlSerializer(val out: Writer) extends EventHandler {
  private var input: String = null
  private var delayedTag: String = null

  def reset(string: String) {
    writeOutput("<?xml version=\"1.0\" encoding=\"UTF-8\"?" + ">")
    input = string
  }

  def startNonterminal(name: String, begin: Int) {
    if (delayedTag != null) {
      writeOutput("<")
      writeOutput(delayedTag)
      writeOutput(">")
    }
    delayedTag = name
  }

  def endNonterminal(name: String, end: Int) {
    if (delayedTag != null) {
      delayedTag = null
      writeOutput("<")
      writeOutput(name)
      writeOutput("/>")
    }
    else {
      writeOutput("</")
      writeOutput(name)
      writeOutput(">")
    }
  }

  def terminal(name: String, begin: Int, end: Int) {
    val tag = if (name(0) == '\'') "TOKEN" else name
    startNonterminal(tag, begin)
    characters(begin, end)
    endNonterminal(tag, end)
  }

  def whitespace(begin: Int, end: Int) {
    characters(begin, end)
  }

  private def characters(begin: Int, end: Int) {
    if (begin < end) {
      if (delayedTag != null) {
        writeOutput("<")
        writeOutput(delayedTag)
        writeOutput(">")
        delayedTag = null
      }
      writeOutput(input.substring(begin, end)
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;"))
    }
  }

  def writeOutput(content: String) {
    out.write(content)
  }
}

