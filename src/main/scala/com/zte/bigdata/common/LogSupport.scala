package com.zte.bigdata.common

import org.slf4j.LoggerFactory

trait LogSupport {
  protected val log =  LoggerFactory.getLogger(this.getClass)
}
