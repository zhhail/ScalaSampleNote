package com.zte.bigdata.xmlreader.common

trait WithMultiThread {
  def withMultiThread(threadPoolSize: Int)(fs: Iterable[Runnable]): Unit = {
    import java.util.concurrent.{Executors, TimeUnit}
    val fixedThreadPool = Executors.newFixedThreadPool(threadPoolSize)
    fs.foreach(fixedThreadPool.execute)
    fixedThreadPool.shutdown()
    fixedThreadPool.awaitTermination(60, TimeUnit.MINUTES)
  }
}
