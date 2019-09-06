package com.zte.bigdata

import java.lang.{Double => JDouble}

import org.apache.hadoop.hive.ql.exec.UDF

import scala.collection.JavaConversions._

class TakenAndAverage extends UDF {
  def evaluate(data: java.util.List[java.lang.Double], num: Integer): JDouble =
    if (data == null || num == null) null
    else {
      val ls = data.take(num)
      ls.map(_.doubleValue()).sum / ls.length
    }
}

