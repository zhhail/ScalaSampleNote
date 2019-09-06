package com.zte.bigdata

import com.zte.bigdata.common.UnitTest

class IsDistanceLessThan10kmTest extends UnitTest {

  private val sampleData: Vector[(Boolean, (Double, Double, Double, Double))] = Vector(
    /* distance, lat1,lon1,lat2,lon2 */
    (false, (12.56, 5, 12.63, 5.2)),
    (false, (5, 1, 5.08983151432766477, 1)),
    (false, (5, 12, 5.8983151432766477, 12)),
    (true, (5, 12, 5.001, 12)),
    (false, (33.5568, 48.666, 35.3349, 48.523)),
    (true, (33.556826, 48.666, 33.5349, 48.623)),
    (false, (2.5607, 5.8, 3.55, 5.2)),
    (false, (-90, -180, -80, -111)),
    (false, (-90, -180, 32.00005, 114.89888))
  )
  private val disFunc = (new  IsDistanceLessThan10km).evaluate _

  private def test(expected: Boolean, in: (Double, Double, Double, Double)) =
    it should s"return $expected from $in" in {
      val flag = disFunc(in._1, in._2, in._3, in._4)
      flag shouldBe expected
    }

  sampleData.foreach(x => test(x._1, x._2))

}
