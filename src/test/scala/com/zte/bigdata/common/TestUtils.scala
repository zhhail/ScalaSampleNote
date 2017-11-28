package com.zte.bigdata.common

trait TestUtils {
  def repeat[T](times: Int)(code: => T): Unit = {
    for (i <- 0 to times) code
  }

  def time[T](msg: String)(code: => T): T = {
    val t0 = System.nanoTime: Double
    val res = code
    val t1 = System.nanoTime: Double
    println(f"time cost: ${(t1 - t0) / 1000000.0}%10.4f ms - $msg ")
    res
  }

  def time[T](times:Int, msg:String)(code: => T): Unit = {
    val t0 = System.nanoTime: Double
    repeat(times)(code)
    val t1 = System.nanoTime: Double
    println(f"time cost: ${(t1 - t0) / 1000000.0}%10.4f ms - $times times $msg ")
  }
}
