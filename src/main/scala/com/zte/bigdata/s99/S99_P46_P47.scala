package com.zte.bigdata.s99


trait S99_P46_P47 {
//  P46 (**) Truth tables for logical expressions.
  class MyBool(b:Boolean) {
    def and(a:Boolean):Boolean = LogicalOperation.and(a,b)
    def or(a:Boolean):Boolean = LogicalOperation.or(a,b)
    def xor(a:Boolean):Boolean = LogicalOperation.xor(a,b)
    def nand(a:Boolean):Boolean = LogicalOperation.nand(a,b)
    def nor(a:Boolean):Boolean = LogicalOperation.nor(a,b)
    def xnor(a:Boolean):Boolean = LogicalOperation.xnor(a,b)
  }
  implicit def Boolean2MyBool(b:Boolean) = new MyBool(b)

  // val f = (a: Boolean, b: Boolean) => and(or(a, b), nand(a, b))
  def table2(f:(Boolean, Boolean)=>Boolean):List[String] = {
    val head = "A      B      Result"
//    println("true  true  " + f(true,true))
//    println("true  false " + f(true,false))
//    println("false true  " + f(false,true))
//    println("false false " + f(false,false))
    val set=List(true,false)
    val context = set.flatMap(x=>set.map(y=>(x,y))).map(e=>f"${e._1}%-7s${e._2}%-7s${f(e._1,e._2)}")
    head::context
  }

  object LogicalOperation {
    def not(c:Boolean) = if(c)false else true
    def and(c1:Boolean, c2:Boolean) = {
      if (c1) if (c2) true else false
      else false
    }
    def or(c1:Boolean, c2:Boolean) = {
      if (!c1) if (!c2) false else true
      else true
    }
    def xor(c1:Boolean, c2:Boolean) = {
      if (c1 != c2) true else false
    }
    def nand(c1:Boolean, c2:Boolean) = {
      if (c1) if (c2) false else true
      else true
    }
    def nor(c1:Boolean, c2:Boolean) = {
      if (!c1) if (!c2) true else false
      else false
    }
    def xnor(c1:Boolean, c2:Boolean) = {
      if (c1 != c2) false else true
    }
  }
}
