package com.zte.bigdata.common

import javax.xml.parsers.{SAXParser, SAXParserFactory}
import javax.xml.stream.XMLInputFactory

object ThreadValueFactory {
  // First create a new XMLInputFactory
  private val inputFactory_thread: ThreadLocal[XMLInputFactory] = new ThreadLocal[XMLInputFactory]() {
    override protected def initialValue: XMLInputFactory = {
      val v = XMLInputFactory.newInstance()
      v.setProperty("javax.xml.stream.isCoalescing", true) //否则读取多行数据时出问题
      v
    }
  }

  def inputFactory: XMLInputFactory = inputFactory_thread.get()

  private val saxparser_thread: ThreadLocal[SAXParser] = new ThreadLocal[SAXParser]() {
    override protected def initialValue: SAXParser = SAXParserFactory.newInstance().newSAXParser()

  }

  def saxparser: SAXParser = saxparser_thread.get()
}
