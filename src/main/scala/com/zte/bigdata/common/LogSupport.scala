package com.zte.bigdata.common

import org.slf4j.{Logger, LoggerFactory}

trait LogSupport {
  protected val log: Logger =  LoggerFactory.getLogger(this.getClass)
}
