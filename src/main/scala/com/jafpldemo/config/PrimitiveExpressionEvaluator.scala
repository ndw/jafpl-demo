package com.jafpldemo.config

import com.jafpl.messages.{ItemMessage, Message, Metadata}
import com.jafpl.runtime.ExpressionEvaluator

class PrimitiveExpressionEvaluator() extends ExpressionEvaluator() {
  override def newInstance(): PrimitiveExpressionEvaluator = {
    new PrimitiveExpressionEvaluator()
  }

  override def value(expr: Any, context: List[Message], bindings: Map[String,Message], options: Option[Any]): List[Message] = {
    List(new ItemMessage(expr, Metadata.ANY))
  }

  override def booleanValue(expr: Any, context: List[Message], bindings: Map[String,Message], options: Option[Any]): Boolean = {
    ! ( (expr == "") || (expr == "false") || (expr == "0") )
  }

  override def singletonValue(expr: Any, context: List[Message], bindings: Map[String, Message], options: Option[Any]): Message = {
    value(expr, context, bindings, options).head
  }
}
