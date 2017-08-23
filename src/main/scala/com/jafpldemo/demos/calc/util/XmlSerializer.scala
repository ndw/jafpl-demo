package com.jafpldemo.demos.calc.util

import java.io.Writer

import com.jafpldemo.demos.calc.ExpressionParser.EventHandler

// This file was generated by REx v5.45 which is Copyright (c) 1979-2017 by Gunther Rademacher <grd@gmx.net>

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

