package com.zte.bigdata.common
import java.lang.{Double => JDouble}

trait DistanceOfLatLonImpl {
  def distance(lat1: JDouble, lon1: JDouble, lat2: JDouble, lon2: JDouble): JDouble = {
    if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) null
    else distance_Double(lat1, lon1, lat2, lon2)
  }
  def distance_Int(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Int =  distance_Double(lat1,lon1,lat2,lon2).toInt
  def distance_Double(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double = {
    val List(lat1_rad, lon1_rad, lat2_rad, lon2_rad) = List(lat1, lon1, lat2, lon2).map(rad)
    val d = 6378.138 * 2 * 1000 // 地球直径
    val latDiff = Math.sin(lat1_rad / 2 - lat2_rad / 2)
    val lonDiff = Math.sin(lon1_rad / 2 - lon2_rad / 2)

    d * Math.asin(Math.sqrt(latDiff * latDiff + Math.cos(lat1_rad) * Math.cos(lat2_rad) * lonDiff * lonDiff))
  }

//  网上公式对应的算法
//  def distance1(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double = {
//    val lat1_rad = rad(lat1)
//    val lat2_rad = rad(lat2)
//    val c = Math.sin(lat1_rad) * Math.sin(lat2_rad) + math.cos(lat1_rad) * Math.cos(lat2_rad) * Math.cos(rad(lon1 - lon2))
//    val r = 6378.138 * 1000
//    r * Math.acos(c)
//  }

  private def rad(deg: Double) = deg * Math.PI / 180
}

// 代码中公式
//6378.138*2*ASIN(SQRT(power(SIN((a.MRLat*PI()/180-b.celllat*PI()/180)/2),2)+COS(a.MRLat*PI()/180)*COS(b.celllat*PI()/180)*power(SIN((a.MRLon*PI()/180-b.celllon*PI()/180)/2),2)))*1001

// 网上公式
//C = sin(LatA*Pi/180)*sin(LatB*Pi/180) + cos(LatA*Pi/180)*cos(LatB*Pi/180)*cos((MLonA-MLonB)*Pi/180)
//Distance = R*Arccos(C)*Pi/180