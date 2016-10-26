package com.zte.bigdata.xmlreader.common.scalareader

import scala.xml.{Elem, XML}
import com.zte.bigdata.xmlreader.common.NorthMR_XML_Info

trait NorthMR_XML_Reader_ScalaDomImpl extends NorthMR_XML_Reader_ScalaImpl {
  this : NorthMR_XML_Info =>
  def loadfile(xmlFileName: String): Elem = XML.load(xmlFileName)
}

