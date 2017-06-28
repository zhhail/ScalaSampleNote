package com.zte.bigdata.four_operations_parser

import org.scalatest.{ShouldMatchers, FunSpec}


class ParserTest extends FunSpec with ShouldMatchers {
  val parser = new Parser {}

  import parser.expr

  describe("从字符串中产生函数"){
    def funFactory(str:String):Int=>Int = {
      x:Int => expr(str.replace("x",x.toString))
    }
    val f1 = funFactory("1+ x*3")
    f1(2) shouldBe 7
    f1(3) shouldBe 10
    val f2 = funFactory("x+(2*(3-6 /  (3+2)))")
    f2(2) shouldBe 6
  }

  describe("混合运算") {
    it("基本混合运算") {
      expr("1+2*3") shouldBe 7
      expr("9-5-2-1") shouldBe 1
      expr("5-9+8") shouldBe 4
      expr("7+8-43+6+6") shouldBe -16
      expr("5-9*8") shouldBe -67
      expr("5-879*88/5") shouldBe -15465
      expr("1+  2*3") shouldBe 7
      expr("3+    5+7") shouldBe 15
    }
    it("终极混合运算") {
      expr("1+2*(3+4*(5+6))") shouldBe 95
      expr("1+55+3*2*1") shouldBe 62
      expr("2+(2*(3-6/(3+2)))") shouldBe 6
      expr("5-65/3/2") shouldBe -5
      expr("  5*(31+2 ) ") shouldBe 165
      expr("5*(3+9    +(3*2)-5)") shouldBe 65
      expr("(   (( 7111+ 18) *6/7))") shouldBe 6110
      expr("2+(2*(3-6 /  (3+2)))") shouldBe 6
      expr("1+(2+3  )+(3*4)  ") shouldBe 18
      expr("(((3+2)    )*3)  +4 ") shouldBe 19
    }
  }
  describe("加减乘除单独运算") {
    it("单加 a")(expr("1+2") shouldBe 3)
    it("单加 b")(expr("7 + 9") shouldBe 16)
    it("单加 c")(expr(" 7 + 9 ") shouldBe 16)
    it("单减 a")(expr("3-2") shouldBe 1)
    it("单减 b")(expr("7 - 9") shouldBe -2)
    it("单减 c")(expr(" 7 - 9 ") shouldBe -2)
    it("单乘 a")(expr("3*2") shouldBe 6)
    it("单乘 b")(expr("7 * 9") shouldBe 63)
    it("单乘 c")(expr(" 7 * 9 ") shouldBe 63)
    it("单除 a")(expr("3/2") shouldBe 1)
    it("单除 b")(expr("27 / 9") shouldBe 3)
    it("单除 c")(expr(" 77 / 7 ") shouldBe 11)
    it("无运算 a")(expr(" 3 ") shouldBe 3)
    it("无运算 b")(expr(" (3) ") shouldBe 3)
  }

}
