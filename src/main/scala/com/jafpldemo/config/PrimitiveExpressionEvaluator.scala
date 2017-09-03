package com.jafpldemo.config

import com.jafpl.runtime.ExpressionEvaluator

class PrimitiveExpressionEvaluator() extends ExpressionEvaluator() {
  override def value(expr: Any, context: List[Any], bindings: Map[String,Any]): Any = {
    expr
  }

  override def booleanValue(expr: Any, context: List[Any], bindings: Map[String,Any]): Boolean = {
    ! ( (expr == "") || (expr == "false") || (expr == "0") )
  }
}
