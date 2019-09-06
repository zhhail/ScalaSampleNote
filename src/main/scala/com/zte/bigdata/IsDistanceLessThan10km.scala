package com.zte.bigdata

import org.apache.hadoop.hive.ql.exec.UDF
import java.lang.{Boolean => JBoolean, Double => JDouble}

import com.zte.bigdata.common.LatLonOf10km

class IsDistanceLessThan10km extends UDF {
  def evaluate(lat1: JDouble, lon1: JDouble, lat2: JDouble, lon2: JDouble):JBoolean =
    if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) null
    else   LatLonOf10km.isLessThan10km(lat1, lon1, lat2, lon2)
}
