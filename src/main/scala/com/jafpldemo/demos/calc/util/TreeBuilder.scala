package com.jafpldemo.demos.calc.util

import com.jafpldemo.demos.calc.ExpressionParser.EventHandler

import scala.collection.mutable

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
