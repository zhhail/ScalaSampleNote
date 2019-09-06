package com.zte.bigdata.common

import org.slf4j.LoggerFactory


trait LogSupport {
  protected val log = LoggerFactory.getLogger("dbscan")
  private var t = System.nanoTime: Double
}

trait TestUtils extends LogSupport{
  def time[T](msg: String)(code: => T): T = time(msg, 1)(code)

  // 只适合测试运算量较大的函数， 如果 code 很简单，叫名参数的开销将会成为主要开销
  def time[T](msg: String, times: Long)(code: => T): T = {
    val t0 = System.nanoTime: Double
    var res = code
    var i = 1
    while (i < times) {
      i += 1
      res = code
    }
    val t1 = System.nanoTime: Double
    log.error(f"cost: ${(t1 - t0) / 1000000000.0}%12.3f s - run ($msg) $times times ")
    res
  }

  def time[T](times: Long, msg: String)(code: => T): T = time(msg, times)(code)

}
