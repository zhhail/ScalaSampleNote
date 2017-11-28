package com.zte.bigdata.xmlreader.sax

import java.io.FileInputStream

import com.zte.bigdata.common.ThreadValueFactory
import com.zte.bigdata.xmlreader.common.NorthMR_XML_Info
import com.zte.bigdata.xmlreader.scalareader.NorthMR_XML_Reader_ScalaImpl
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream

import scala.xml.Elem
import scala.xml.XML._

class NorthMR_XML_Reader_ScalaSaxImpl extends NorthMR_XML_Reader_ScalaImpl {
  this : NorthMR_XML_Info =>
  // 未实际测试，感觉像是假的sax
  def loadfile(xmlFileName: String): Elem = {
    using(new FileInputStream(xmlFileName)) {
      gz => using(new GzipCompressorInputStream(gz))(withSAXParser(ThreadValueFactory.saxparser).load)
    }
  }
}
