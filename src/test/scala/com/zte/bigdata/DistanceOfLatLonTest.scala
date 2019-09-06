package com.zte.bigdata

import com.zte.bigdata.common.{LatLonOf10km, TestUtils, UnitTest}

class DistanceOfLatLonTest extends UnitTest with TestUtils {
  private val latLonOf10km = LatLonOf10km.mapOf10km.filter(_._1 != 90).map(x => (x._1.toDouble, 0d, x._1.toDouble, x._2))
  private val disFunc = (new DistanceOfLatLon).evaluate _
  private val delta = 0.001
  it should "return 10000" in {
    val expectedDis = 10 * 1000
    val dis = latLonOf10km.map(x => disFunc(x._1, x._2, x._3, x._4))
    //    dis.map(x=>Math.abs(expectedDis-x)/expectedDis*100).zipWithIndex.toVector.sorted.reverse.foreach(println)
    val maxdiff = dis.map(x => Math.abs(x - expectedDis)).max

    maxdiff < delta * expectedDis shouldBe true
  }
  private val sampleData: Vector[(Double, (Double, Double, Double, Double))] = Vector(
    /* distance, lat1,lon1,lat2,lon2 */
    (23.0832 * 1000, (12.56, 5, 12.63, 5.2)),
    (198.3719 * 1000, (33.5568, 48.666, 35.3349, 48.523)),
    (4.6769 * 1000, (33.556826, 48.666, 33.5349, 48.623)),
    (128.7502 * 1000, (2.5607, 5.8, 3.55, 5.2)),
    (1113195.0824656615,(-90,-180,-80,-111)),
    (1.3580985572056474E7,(-90,-180,32.00005,114.89888))
  )

  private def getdis10SampleData():Unit = {
    def getdis(lat:Double,targetDis:Double = 10*1000) ={
      val r = 6378.138 * 1000
      val actL = r*Math.cos(lat*Math.PI/180)
      val target = 10*1000
      val latdiff = target/actL
      latdiff*180/Math.PI
    }
    (0 to 89).map(x=>getdis(x.toDouble)).zipWithIndex.map(x=>(x._2,x._1)).foreach(x=>println(s"$x,"))
  }

  sampleData.foreach(x => test(x._1, x._2))

  private def test(expected: Double, in: (Double, Double, Double, Double)) =
    it should s"return $expected" in {
      val dis = disFunc(in._1, in._2, in._3, in._4)
      val res = Math.abs(dis - expected) / expected < delta
      if(!res) println(dis)
      res shouldBe true
    }
}

