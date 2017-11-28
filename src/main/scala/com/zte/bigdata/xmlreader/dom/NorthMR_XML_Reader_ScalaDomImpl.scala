package com.zte.bigdata.xmlreader.dom

import java.io.FileInputStream

import com.zte.bigdata.xmlreader.common.NorthMR_XML_Info
import com.zte.bigdata.xmlreader.scalareader.NorthMR_XML_Reader_ScalaImpl
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream

import scala.xml.{Elem, XML}

trait NorthMR_XML_Reader_ScalaDomImpl extends NorthMR_XML_Reader_ScalaImpl {
  this : NorthMR_XML_Info =>
  def loadfile(xmlFileName: String): Elem = {
    using(new FileInputStream(xmlFileName)) {
      gz => using(new GzipCompressorInputStream(gz))(XML.load)
    }
  }
}

