package com.zte.bigdata.four_operations_parser

trait Parser {
  def expr(str: String): Int = {
    i = 0
    expr_add(str)
  }

  private var i = 0

  private def expr_add(str: String): Int = {
    var result = expr_mul(str)
    var continue = true
    while (i < str.length && continue) {
      if (getOperator(str) == '+') {
        i += 1
        result += expr_mul(str)
      }
      else if (getOperator(str) == '-') {
        i += 1
        result -= expr_mul(str)
      }
      else continue = false
    }
    result
  }

  private def expr_mul(str: String): Int = {
    var result = expr_brace(str)
    var continue = true
    while (i < str.length && continue) {
      if (getOperator(str) == '*') {
        i += 1
        result *= expr_brace(str)
      }
      else if (getOperator(str) == '/') {
        i += 1
        result /= expr_brace(str)
      }
      else continue = false
    }
    result
  }

  private def expr_brace(str: String): Int = {
    var result = 0
    if (getOperator(str) == '(') {
      i += 1
      result = expr_add(str)
      i += 1
    }
    else {
      result = asc2i(str)
    }
    result
  }

  private def asc2i(str: String): Int = {
    val dig = str.drop(i).takeWhile(_.isDigit)
    i += dig.length
    if (dig.length == 0) 0 else dig.toInt
  }

  private def getOperator(str: String): Char = {
    val blank = str.drop(i).takeWhile(_ == ' ')
    i += blank.length
    if (i < str.length-1)str(i) else '#'
  }
}
