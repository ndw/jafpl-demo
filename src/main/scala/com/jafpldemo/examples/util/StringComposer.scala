package com.jafpldemo.examples.util

import com.jafpl.exceptions.StepException
import com.jafpl.messages.{ItemMessage, Metadata}
import com.jafpl.steps.{ViewportComposer, ViewportItem}

import scala.collection.mutable.ListBuffer

class StringComposer extends ViewportComposer {
  private var metadata: Metadata = Metadata.BLANK
  private val items = ListBuffer.empty[StringViewportItem]
  private var suffix = ""

  override def decompose(item: Any, metadata: Metadata): List[ViewportItem] = {
    item match {
      case stringItem: String =>
        this.metadata = metadata
        var s = stringItem
        val nextWord = "(\\W*)(\\w+)(.*)".r
        var more = true
        while (more) {
          s match {
            case nextWord(prefix,word,rest) =>
              items += new StringViewportItem(prefix, word, Metadata.STRING)
              s = rest
            case _ =>
              suffix = s
              more = false
          }
        }

      case _ => throw new StepException("UnexpectedType", s"Unexpected item type: $item")
    }

    items.toList
  }

  override def recompose(): ItemMessage = {
    var wholeItem = ""
    for (item <- items) {
      wholeItem += item.prefix
      for (s <- item.transformedItems) {
        wholeItem += s
      }
    }
    wholeItem += suffix
    new ItemMessage(wholeItem, metadata)
  }
}
