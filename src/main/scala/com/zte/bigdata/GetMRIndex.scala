package com.zte.bigdata

import org.apache.hadoop.hive.ql.exec.UDF

import scala.collection.JavaConversions._
import scala.collection.mutable

class GetMRIndex extends UDF {
  /** ***********************************************
    * 定位精度   定位类型            说明
    * 超高       0,100
    * 高        101,102           指纹定位剥离出来
    * 中        1,2,3,103
    * 低        4,6,7,104
    * 超低      5,8               可能出现射线和画圈的样本
    * 无        99,125,126,127
    * **********************************************/
  private val locationMap: Map[Int, Int] = Map(0 -> 0,
    100 -> 1,
    101 -> 10,
    102 -> 10,
    99 -> Int.MaxValue,
    125 -> Int.MaxValue,
    126 -> Int.MaxValue,
    127 -> Int.MaxValue
  )

  def evaluate(eventTime: java.lang.Long, mrTime: java.util.List[java.lang.Long], locationType: java.util.List[Integer]): Integer = {
    if (eventTime == null || mrTime == null || locationType == null || mrTime.isEmpty || mrTime.length != locationType.length) null
    else if (mrTime.size() == 1) 0
    else {
      val diffTime = mrTime.map(x => Math.abs(x - eventTime))
      val locationLevel = locationType.map{
        x=> if(x == null) Int.MaxValue else locationMap.getOrElse(x, 1000)
      }
      var diff = Int.MaxValue.toLong
      var loctype = Int.MaxValue
      var idx = 0
      for (index <- diffTime.indices) {
        if ((diffTime(index) < diff && locationLevel(index) <= loctype) || locationLevel(index) < loctype) {
          diff = diffTime(index)
          loctype = locationLevel(index)
          idx = index
        }
      }
      idx
    }
  }
}
