package com.zte.bigdata

import com.zte.bigdata.common.UnitTest

class GridSizeConvertTest extends UnitTest {
  private val trans = new GridSizeConvert

  private def test(min: Int, max: Int, toSize: Int): Unit =
    it should s"$min to $max -> $toSize" in {
      val input = min to max filter (_ != 0)
      val result = input.map(x => (trans.evaluate(x, toSize), x)).groupBy(x => x._1)
      //    result.map(x=>(x._1,x._2.map(_._2))).toList.sortBy(_._1).foreach(println)
      val invalid = result.map(x => (x._1, x._2.length)).filter(_._2 != toSize)
      val m = if (input.max > 0) (input.max - 1) / toSize + 1 else (input.max + 1) / toSize - 1
      val n = if (input.min > 0) (input.min - 1) / toSize + 1 else (input.min + 1) / toSize - 1
      if (invalid.keys.exists(x => x != m && x != n)) {
        println("invalid grid: ")
        invalid.toList.sorted.foreach(println)
      }

      invalid.keys.exists(x => x != m && x != n) shouldBe false
    }

  test(-30, 1000, 10)
  test(-100, 455, 23)
  test(-3000, -1440, 50)
  test(100, 999, 100)
  test(100, 1000, 100)
  test(100, 1001, 100)
  test(-5500, -999, 100)
  test(-5500, -1000, 100)
  test(-5500, -1001, 100)
  test(-5499, -1001, 100)
  test(-5501, -1001, 100)

  it should "test single grid" in {

    trans.evaluate(1, 2) shouldBe 1
    trans.evaluate(2, 2) shouldBe 1
    trans.evaluate(3, 2) shouldBe 2
    trans.evaluate(4, 2) shouldBe 2
    trans.evaluate(5, 2) shouldBe 3
    trans.evaluate(-1, 2) shouldBe -1
    trans.evaluate(-2, 2) shouldBe -1
    trans.evaluate(-3, 2) shouldBe -2
    trans.evaluate(-4, 2) shouldBe -2
    trans.evaluate(-5, 2) shouldBe -3
  }

}
