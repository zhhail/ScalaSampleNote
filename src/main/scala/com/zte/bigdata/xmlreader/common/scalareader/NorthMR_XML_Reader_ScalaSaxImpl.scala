package com.zte.bigdata.xmlreader.common.scalareader

import scala.xml.XML._
import javax.xml.parsers.SAXParserFactory
import scala.xml.Elem
import com.zte.bigdata.xmlreader.common.NorthMR_XML_Info

class NorthMR_XML_Reader_ScalaSaxImpl extends NorthMR_XML_Reader_ScalaImpl {
  this : NorthMR_XML_Info =>
  // 未测试，感觉像是假的sax
  def loadfile(xmlFileName: String): Elem =
    withSAXParser(SAXParserFactory.newInstance().newSAXParser()).load(xmlFileName)
}
