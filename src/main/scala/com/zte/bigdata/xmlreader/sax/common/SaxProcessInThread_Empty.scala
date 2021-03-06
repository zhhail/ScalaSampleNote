package com.zte.bigdata.xmlreader.sax.common

import java.io.OutputStreamWriter

import com.zte.bigdata.xmlreader.common.{NorthMR_XML_Info, NorthMR_XML_Info_ZTE}

class SaxProcessInThread_Empty(xmlFileName: String, fileWriter: OutputStreamWriter) extends SaxProcessInThread(xmlFileName, fileWriter) {
  override def getSaxProcessor(xmlFileName: String, fileWriter: OutputStreamWriter): MySAXHandler with NorthMR_XML_Info = xmlFileName.split("/").last match {
    case zte: String if zte.startsWith("FDD-LTE_MRO_ZTE") => new MySAXHandler_empty(fileWriter) with NorthMR_XML_Info_ZTE
    case _ => new MySAXHandler_empty(fileWriter) with NorthMR_XML_Info
  }

}
