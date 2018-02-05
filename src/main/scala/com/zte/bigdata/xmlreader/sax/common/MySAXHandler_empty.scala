package com.zte.bigdata.xmlreader.sax.common

import java.io.OutputStreamWriter

import com.zte.bigdata.xmlreader.common.{NorthMR_XML_Info, ResultWriter}
import org.xml.sax.{Attributes, Locator}

class MySAXHandler_empty(fileWriter: OutputStreamWriter) extends MySAXHandler(fileWriter) {
  this: NorthMR_XML_Info with ResultWriter =>

  override def startElement(uri: String,
                            localName: String,
                            qName: String,
                            attributes: Attributes): Unit = {}

  override def endElement(uri: String,
                          localName: String,
                          qName: String): Unit = {}

  override def characters(ch: Array[Char], start: Int, length: Int): Unit = {}

//  override def setDocumentLocator(locator: Locator):Unit = print(".")
}
