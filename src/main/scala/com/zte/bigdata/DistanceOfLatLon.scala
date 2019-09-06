package com.zte.bigdata

import java.lang.{Double => JDouble}

import com.zte.bigdata.common.DistanceOfLatLonImpl
import org.apache.hadoop.hive.ql.exec.UDF

class DistanceOfLatLon extends UDF with DistanceOfLatLonImpl {
  def evaluate(lat1: JDouble, lon1: JDouble, lat2: JDouble, lon2: JDouble): JDouble =
    distance(lat1, lon1, lat2, lon2)
}
/* 测试
CREATE TEMPORARY FUNCTION dis as 'com.zte.bigdata.DistanceOfLatLon';
select dis(1,1,1,1);
select dis(1,1,1,null);
 */