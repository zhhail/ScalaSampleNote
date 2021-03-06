package com.zte.bigdata

import java.text.SimpleDateFormat

import com.zte.bigdata.common.{JSONUtil, TestUtils, UnitSpec, strSplit}

class SomethingTest extends UnitSpec with TestUtils with JSONUtil{
  describe("String Util split") {
    it("test split") {
      strSplit("ab,cd,ef") shouldBe List("ab", "cd", "ef")
      strSplit("ab,,ef") shouldBe List("ab", "", "ef")
      strSplit("ab,,ef,,") shouldBe List("ab", "", "ef", "", "")
      strSplit( """ab,"cd,ef",""") shouldBe List("ab", "\"cd,ef\"", "")
      strSplit( """ab,"cd,ef",gg,,hh""") should be(List("ab", "\"cd,ef\"", "gg", "", "hh"))

    }
  }
  describe("xxx"){
    it("from json to Map[String,Any]") {
      val json = """{"operateType":"decrypt","password":"ajIV2cPwlJLWoA0q9Lel5g=="}"""
      val r = fromJSONOption[Map[String, Any]](json).get.map(x=>(x._1,x._2.toString))
      println(r)
    }
    it("from json to Map[String,Any] 2") {
      val json = "ZXvmax_2017"
      val r = fromJSONOption[Map[String, Any]](json).getOrElse(Map()).map(x=>(x._1,x._2.toString))
      println(r)
    }
    it("from json to String") {
      val json = "ZXvmax_2017"
      val r = fromJSONOption[Any](json).getOrElse("").toString
      println(r)
    }
  }

  val datetime = "2016-08-17T07:30:54.080"
  val sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  val dStart: Long = sDateFormat.parse("2000-01-01 00:00:00").getTime

  describe("性能对比测试") {
    it("") {
      val times1 = 1000000*30
      time(times1, "toInt toString")("020".toInt.toString)
      time(times1, "drop")("020".dropWhile(_ == '0'))


      val times2 = 1000000
      time( times2, "gettime local") {
        val sDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        (sDateFormat1.parse(datetime.replace("T", " ")).getTime - dStart) / 1000
      }
      time(times2, "gettime") {
        (sDateFormat.parse(datetime.replace("T", " ")).getTime - dStart) / 1000
      }
      time(times2, "gettime1") {
        (sDateFormat.parse(datetime.take(10) + " " + datetime.drop(11)).getTime - dStart) / 1000
      }
    }
  }
}
