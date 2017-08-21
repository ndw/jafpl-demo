package com.jafpldemo.demos.calc

// This file was generated on Sun Aug 20, 2017 18:01 (UTC-05) by REx v5.45 which is Copyright (c) 1979-2017 by Gunther Rademacher <grd@gmx.net>
// REx command line: expressions.ebnf -tree -scala

// Hacked to change the class/object names and the primary entry point method name.

import collection.mutable.ArrayBuffer

class ExpressionParser {

  def this(string: String, eh: ExpressionParser.EventHandler) {
    this
    initialize(string, eh)
  }

  def initialize(string: String, eh: ExpressionParser.EventHandler) {
    eventHandler = eh
    input = string
    size = input.length
    reset(0, 0, 0)
  }

  def reset(l: Int, b: Int, e: Int) {
    b0 = b; e0 = b
    l1 = l; b1 = b; e1 = e
    end = e
    eventHandler.reset(input)
  }

  def reset {
    reset(0, 0, 0)
  }

  def parse {
    eventHandler.startNonterminal("Expression", e0)
    lookahead1W(3)                  // Literal | Name | S^WhiteSpace | '$' | '(' | '+' | '-'
    whitespace
    parse_AdditiveExpr
    consume(1)                      // EOF
    eventHandler.endNonterminal("Expression", e0)
  }

  private def parse_ParenthesizedExpr {
    eventHandler.startNonterminal("ParenthesizedExpr", e0)
    consume(7)                      // '('
    lookahead1W(3)                  // Literal | Name | S^WhiteSpace | '$' | '(' | '+' | '-'
    whitespace
    parse_AdditiveExpr
    consume(8)                      // ')'
    eventHandler.endNonterminal("ParenthesizedExpr", e0)
  }

  private def parse_AdditiveExpr {
    eventHandler.startNonterminal("AdditiveExpr", e0)
    parse_MultiplicativeExpr
    var c1 = true
    while (c1) {
      if (l1 != 10                  // '+'
        && l1 != 12) {               // '-'
        c1 = false
      }
      else {
        l1 match {
          case 10 =>                  // '+'
            consume(10)               // '+'
          case _ =>
            consume(12)               // '-'
        }
        lookahead1W(3)              // Literal | Name | S^WhiteSpace | '$' | '(' | '+' | '-'
        whitespace
        parse_MultiplicativeExpr
      }
    }
    eventHandler.endNonterminal("AdditiveExpr", e0)
  }

  private def parse_MultiplicativeExpr {
    eventHandler.startNonterminal("MultiplicativeExpr", e0)
    parse_UnaryExpr
    var c1 = true
    while (c1) {
      lookahead1W(5)                // EOF | S^WhiteSpace | '%' | ')' | '*' | '+' | ',' | '-' | '/'
      if (l1 != 6                   // '%'
        && l1 != 9                   // '*'
        && l1 != 13) {               // '/'
        c1 = false
      }
      else {
        l1 match {
          case 9 =>                   // '*'
            consume(9)                // '*'
          case 13 =>                  // '/'
            consume(13)               // '/'
          case _ =>
            consume(6)                // '%'
        }
        lookahead1W(3)              // Literal | Name | S^WhiteSpace | '$' | '(' | '+' | '-'
        whitespace
        parse_UnaryExpr
      }
    }
    eventHandler.endNonterminal("MultiplicativeExpr", e0)
  }

  private def parse_UnaryExpr {
    eventHandler.startNonterminal("UnaryExpr", e0)
    if (l1 == 10                    // '+'
      || l1 == 12) {                 // '-'
      l1 match {
        case 12 =>                    // '-'
          consume(12)                 // '-'
        case _ =>
          consume(10)                 // '+'
      }
    }
    lookahead1W(2)                  // Literal | Name | S^WhiteSpace | '$' | '('
    whitespace
    parse_ValueExpr
    eventHandler.endNonterminal("UnaryExpr", e0)
  }

  private def parse_ValueExpr {
    eventHandler.startNonterminal("ValueExpr", e0)
    l1 match {
      case 2 =>                       // Literal
        consume(2)                    // Literal
      case 5 =>                       // '$'
        parse_VarRef
      case 7 =>                       // '('
        parse_ParenthesizedExpr
      case _ =>
        parse_FunctionCall
    }
    eventHandler.endNonterminal("ValueExpr", e0)
  }

  private def parse_FunctionCall {
    eventHandler.startNonterminal("FunctionCall", e0)
    consume(3)                      // Name
    lookahead1W(1)                  // S^WhiteSpace | '('
    consume(7)                      // '('
    lookahead1W(4)                  // Literal | Name | S^WhiteSpace | '$' | '(' | ')' | '+' | '-'
    if (l1 != 8) {                  // ')'
      whitespace
      parse_AdditiveExpr
      var c1 = true
      while (c1) {
        if (l1 != 11) {             // ','
          c1 = false
        }
        else {
          consume(11)               // ','
          lookahead1W(3)            // Literal | Name | S^WhiteSpace | '$' | '(' | '+' | '-'
          whitespace
          parse_AdditiveExpr
        }
      }
    }
    consume(8)                      // ')'
    eventHandler.endNonterminal("FunctionCall", e0)
  }

  private def parse_VarRef {
    eventHandler.startNonterminal("VarRef", e0)
    consume(5)                      // '$'
    lookahead1W(0)                  // Name | S^WhiteSpace
    consume(3)                      // Name
    eventHandler.endNonterminal("VarRef", e0)
  }

  private def consume(t: Int) {
    if (l1 == t) {
      whitespace
      eventHandler.terminal(ExpressionParser.TOKEN(l1), b1, e1)
      b0 = b1; e0 = e1; l1 = 0
    }
    else {
      error(b1, e1, 0, l1, t)
    }
  }

  private def whitespace {
    if (e0 != b1) {
      eventHandler.whitespace(e0, b1)
      e0 = b1
    }
  }

  private def matchW(set: Int): Int =  {
    var continue = true
    var code = 0
    while (continue) {
      code = matcher(set)
      if (code != 4) {              // S^WhiteSpace
        continue = false
      }
    }
    code
  }

  private def lookahead1W(set: Int) {
    if (l1 == 0) {
      l1 = matchW(set)
      b1 = begin
      e1 = end
    }
  }

  def getErrorMessage(e: ExpressionParser.ParseException) = {
    val tokenSet = ExpressionParser.getExpectedTokenSet(e)
    val found = ExpressionParser.getOffendingToken(e)
    val prefix = input.substring(0, e.begin)
    val line = prefix.replaceAll("[^\n]", "").length + 1
    val column = prefix.length - prefix.lastIndexOf('\n')
    val size = e.end - e.begin
    e.getMessage + (if (found == null) "" else ", found " + found) + "\nwhile expecting " +
      (if (tokenSet.length == 1) tokenSet(0) else "[" + (tokenSet mkString ", ") + "]") + "\n" +
      (if (size == 0 || found != null) "" else "after successfully scanning " + size + " characters beginning ") +
      "at line " + line + ", column " + column + ":\n..." +
      input.substring(e.begin, math.min(input.length, e.begin + 64)) + "..."
  }

  def error(b: Int, e: Int, s: Int, l: Int, t: Int): Int = {
    throw new ExpressionParser.ParseException(b, e, s, l, t)
  }

  private def matcher(tokenSetId: Int) = {
    begin = end
    var current = end
    var result = ExpressionParser.INITIAL(tokenSetId)
    var state = 0
    var code = result & 15

    while (code != 0) {
      var charclass = -1
      var c0 = if (current < size) input(current) else 0
      current += 1
      if (c0 < 0x80) {
        charclass = ExpressionParser.MAP0(c0)
      }
      else if (c0 < 0xd800) {
        val c1 = c0 >> 4
        charclass = ExpressionParser.MAP1((c0 & 15) + ExpressionParser.MAP1((c1 & 63) + ExpressionParser.MAP1(c1 >> 6)))
      }
      else {
        charclass = 0
      }

      state = code
      val i0 = (charclass << 4) + code - 1
      code = ExpressionParser.TRANSITION((i0 & 3) + ExpressionParser.TRANSITION(i0 >> 2))

      if (code > 15) {
        result = code
        code &= 15
        end = current
      }
    }

    result >>= 4
    if (result == 0) {
      end = current - 1
      val c1 = if (end < size) input(end) else 0
      if (c1 >= 0xdc00 && c1 < 0xe000) {
        end -= 1
      }
      error(begin, end, state, -1, -1)
    }
    else {
      if (end > size) end = size
      (result & 15) - 1
    }
  }

  var input: String = null
  var size = 0
  var begin = 0
  var end = 0
  var b0 = 0
  var e0 = 0
  var l1 = 0
  var b1 = 0
  var e1 = 0
  var eventHandler: ExpressionParser.EventHandler = null
}

object ExpressionParser {

  def getOffendingToken(e: ParseException) = {
    if (e.offending < 0) null else TOKEN(e.offending)
  }

  class ParseException(val begin: Int, val end: Int, val state: Int, val offending: Int, val expected: Int) extends RuntimeException {
    override def getMessage = {
      if (offending < 0) "lexical analysis failed" else "syntax error"
    }
  }

  def getExpectedTokenSet(e: ParseException) = {
    if (e.expected < 0) {
      getTokenSet(- e.state)
    }
    else {
      Array(TOKEN(e.expected))
    }
  }

  trait EventHandler {
    def reset(string: String)
    def startNonterminal(name: String, begin: Int)
    def endNonterminal(name: String, end: Int)
    def terminal(name: String, begin: Int, end: Int)
    def whitespace(begin: Int, end: Int)
  }

  class TopDownTreeBuilder extends EventHandler {
    private var input: String = null
    private var stack = new ArrayBuffer[Nonterminal](64)
    private var top = -1

    override def reset(input: String) {
      this.input = input
      top = -1
    }

    override def startNonterminal(name: String, begin: Int) {
      val nonterminal = new Nonterminal(name, begin, begin, new ArrayBuffer[Symbol])
      if (top >= 0) addChild(nonterminal)
      top += 1
      if (top == stack.length) stack += nonterminal else stack(top) = nonterminal
    }

    override def endNonterminal(name: String, end: Int) {
      var nonterminal = stack(top)
      nonterminal.end = end
      if (top > 0) top -= 1
    }

    override def terminal(name: String, begin: Int, end: Int) {
      addChild(new Terminal(name, begin, end))
    }

    override def whitespace(begin: Int, end: Int) {
    }

    private def addChild(s: Symbol) {
      var current = stack(top)
      current.children += s
    }

    def serialize(e: EventHandler) {
      e.reset(input)
      stack(0).send(e)
    }
  }

  abstract class Symbol(n: String, b: Int, e: Int) {
    var name = n
    var begin = b
    var end = e

    def send(e: EventHandler)
  }

  class Terminal(name: String, begin: Int, end: Int) extends Symbol(name, begin, end) {
    override def send(e: EventHandler) {
      e.terminal(name, begin, end)
    }
  }

  class Nonterminal(name: String, begin: Int, end: Int, c: ArrayBuffer[Symbol]) extends Symbol(name, begin, end) {
    var children = c

    override def send(e: EventHandler) {
      e.startNonterminal(name, begin)
      var pos = begin
      for (c <- children) {
        if (pos < c.begin) e.whitespace(pos, c.begin)
        c.send(e)
        pos = c.end
      }
      if (pos < end) e.whitespace(pos, end)
      e.endNonterminal(name, end)
    }
  }

  private def getTokenSet(tokenSetId: Int) = {
    var expected = new ArrayBuffer[String]
    val s = if (tokenSetId < 0) - tokenSetId else INITIAL(tokenSetId) & 15
    var i = 0
    while (i < 14) {
      var j = i
      val i0 = (i >> 5) * 9 + s - 1
      var f = EXPECTED(i0)
      while (f != 0) {
        if ((f & 1) != 0) {
          expected += TOKEN(j)
        }
        f >>>= 1
        j += 1
      }
      i += 32
    }
    expected.toArray
  }

  private final val MAP0 = Array(
    /*   0 */ 13, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0,
    /*  35 */ 0, 2, 3, 0, 0, 4, 5, 6, 7, 8, 9, 0, 10, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 0, 0, 0, 0, 0, 0, 0, 12,
    /*  66 */ 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 0, 0,
    /*  93 */ 0, 0, 12, 0, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12,
    /* 120 */ 12, 12, 12, 0, 0, 0, 0, 0
  )

  private final val MAP1 = Array(
    /*   0 */ 54, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62,
    /*  26 */ 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62,
    /*  52 */ 62, 62, 126, 165, 139, 155, 195, 181, 195, 200, 165, 165, 165, 165, 165, 165, 165, 165, 165, 165, 165,
    /*  73 */ 165, 165, 165, 165, 165, 165, 165, 165, 165, 165, 165, 165, 165, 165, 165, 165, 165, 165, 165, 165, 165,
    /*  94 */ 165, 165, 165, 165, 165, 165, 165, 165, 165, 165, 165, 165, 165, 165, 165, 165, 165, 165, 165, 165, 165,
    /* 115 */ 165, 165, 165, 165, 165, 165, 165, 165, 165, 165, 165, 13, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0,
    /* 143 */ 2, 3, 0, 0, 4, 5, 6, 7, 8, 9, 0, 10, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
    /* 175 */ 0, 0, 0, 0, 0, 0, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 0, 0, 0, 0, 12, 12, 12, 12, 12, 12, 12, 12,
    /* 204 */ 12, 12, 12, 12, 12, 12, 12, 0, 0, 0, 0, 0
  )

  private final val INITIAL = Array(
    /* 0 */ 1, 2, 3, 4, 5, 6
  )

  private final val TRANSITION = Array(
    /*   0 */ 62, 62, 62, 62, 56, 57, 62, 62, 69, 61, 62, 62, 62, 113, 62, 62, 76, 79, 62, 62, 62, 67, 62, 62, 62, 107,
    /*  26 */ 62, 62, 63, 73, 62, 62, 62, 104, 62, 62, 80, 84, 62, 62, 62, 101, 62, 62, 115, 94, 98, 62, 90, 87, 62, 62,
    /*  52 */ 62, 110, 62, 62, 87, 87, 87, 87, 0, 96, 0, 0, 0, 0, 176, 144, 144, 0, 0, 96, 96, 176, 176, 0, 0, 128, 128,
    /*  79 */ 128, 0, 0, 0, 208, 208, 208, 0, 72, 0, 0, 72, 0, 72, 72, 57, 0, 0, 72, 57, 0, 0, 0, 224, 0, 0, 192, 0, 0,
    /* 108 */ 160, 0, 0, 32, 0, 0, 112, 0, 0, 57, 57
  )

  private final val EXPECTED = Array(
    /* 0 */ 24, 144, 188, 5308, 5564, 16210, 16, 8, 4
  )

  private final val TOKEN = Array(
    "(0)",
    "EOF",
    "Literal",
    "Name",
    "S",
    "'$'",
    "'%'",
    "'('",
    "')'",
    "'*'",
    "'+'",
    "','",
    "'-'",
    "'/'"
  )
}

// End
