package com.jafpldemo.examples.util

import com.jafpl.exceptions.StepException
import com.jafpl.steps.ViewportItem

import scala.collection.mutable.ListBuffer

class StringViewportItem(val prefix: String, val item: String) extends ViewportItem {
  private var items = ListBuffer.empty[String]

  def transformedItems: List[String] = items.toList

  override def getItem: Any = item

  override def putItems(xformed: List[Any]): Unit = {
    for (item <- xformed) {
      item match {
        case s: String => items += s
        case _ => throw new StepException("UnexpectedType", s"Unexpected item type: $item")
      }
    }
  }
}
