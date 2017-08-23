package com.jafpldemo.demos.calc.util

import scala.collection.mutable

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
