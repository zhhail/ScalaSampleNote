package com.zte.bigdata

import java.text.SimpleDateFormat

import com.zte.bigdata.common.{TestUtils, UnitTest}

import scala.collection.JavaConversions._

class GetMRIndexTest extends UnitTest with TestUtils {
  private val func = new GetMRIndex
  private val dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")

  def f(eventTIme: String, mrTime: Array[String], locationType: Array[Int]) = {
    val jmrTime: java.util.List[String] = mrTime.toList
    val jlocationType: java.util.List[Integer] = locationType.toList.map(x => new Integer(x))
    func.evaluate(parseTime(eventTIme), jmrTime.map(parseTime), jlocationType)
  }

  private def parseTime(str: String): java.lang.Long =
    dataFormat.parse(s"${str.take(10)} ${str.drop(11)}").getTime

  it should "return 0 in test1" in {
    val eventTime = "2018-05-05T11:11:11.333"
    val mrTimes = Array("2018-05-05T11:11:21.333",
      "2018-05-05T11:11:13.333",
      "2018-05-05T11:11:04.333")
    val locationTypes = Array(0, 101, 555)
    f(eventTime, mrTimes, locationTypes) shouldBe 0
  }
  it should "return 0 in test2" in {
    val eventTime = "2018-05-05T11:11:11.333"
    val mrTimes = Array("2018-05-05T11:11:21.333",
      "2018-05-05T11:11:13.333",
      "2018-05-05T11:11:04.333")
    val locationTypes = Array(101, 102, 333)
    val res = f(eventTime, mrTimes, locationTypes)
    Array(0, 1) should contain(res)
  }
  it should "return 1 in test3" in {
    val eventTime = "2018-05-05T11:11:11.333"
    val mrTimes = Array("2018-05-05T11:11:21.333",
      "2018-05-05T11:11:13.333",
      "2018-05-05T11:11:04.333")
    val locationTypes = Array(5, 100, 3)
    f(eventTime, mrTimes, locationTypes) shouldBe 1
  }
  it should "return 2 in test4" in {
    val eventTime = "2018-05-05T11:11:11.333"
    val mrTimes = Array("2018-05-05T11:11:21.333",
      "2018-05-05T11:11:13.333",
      "2018-05-05T11:11:04.333")
    val locationTypes = Array(5, 4, 100)
    f(eventTime, mrTimes, locationTypes) shouldBe 2
  }
  it should "return 1 in  test5" in {
    val eventTime = "2018-05-05T11:11:11.333"
    val mrTimes = Array("2018-05-05T11:11:21.333",
      "2018-05-05T11:11:13.333",
      "2018-05-05T11:11:04.333")
    val locationTypes = Array(5, 101, 55)
    f(eventTime, mrTimes, locationTypes) shouldBe 1
  }
  it should "return 1 in  test6" in {
    val eventTime = "2018-05-05T11:11:11.333"
    val mrTimes = Array("2018-05-05T11:11:21.333",
      "2018-05-05T11:11:13.333",
      "2018-05-05T11:11:04.333")
    val locationTypes = Array[Int](0, 0, 0)
    f(eventTime, mrTimes, locationTypes) shouldBe 1
  }

}
