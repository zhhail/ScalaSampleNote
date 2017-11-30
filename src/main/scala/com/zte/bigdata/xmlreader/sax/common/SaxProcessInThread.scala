package com.zte.bigdata.xmlreader.sax.common

import java.io.{FileInputStream, OutputStreamWriter}
import java.util.zip.GZIPInputStream

import com.zte.bigdata.common.ThreadValueFactory
import com.zte.bigdata.xmlreader.common.{NorthMR_XML_Info, NorthMR_XML_Info_ZTE, Using}

class SaxProcessInThread(xmlFileName: String, fileWriter: OutputStreamWriter) extends Runnable with Using {
  override def run(): Unit = {
    val saxparser = ThreadValueFactory.saxparser
    val processor = getSaxProcessor(xmlFileName, fileWriter)
    using(new FileInputStream(xmlFileName))(gz => using(new GZIPInputStream(gz, 2048))(saxparser.parse(_, processor)))
  }

  def getSaxProcessor(xmlFileName: String, fileWriter: OutputStreamWriter): MySAXHandler with NorthMR_XML_Info = xmlFileName.split("/").last match {
    case zte: String if zte.startsWith("FDD-LTE_MRO_ZTE") => new MySAXHandler(fileWriter) with NorthMR_XML_Info_ZTE
    case _ => new MySAXHandler(fileWriter) with NorthMR_XML_Info
  }
}
