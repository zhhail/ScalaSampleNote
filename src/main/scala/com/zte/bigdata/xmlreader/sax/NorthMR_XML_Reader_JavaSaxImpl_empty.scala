package com.zte.bigdata.xmlreader.sax

import java.io.{InputStream, OutputStreamWriter}

import com.zte.bigdata.common.ThreadValueFactory
import com.zte.bigdata.xmlreader.common.{NorthMR_XML_Info, NorthMR_XML_Info_ZTE}
import com.zte.bigdata.xmlreader.sax.common.{MySAXHandler_empty, SaxProcessInThread_Empty}


trait NorthMR_XML_Reader_JavaSaxImpl_empty extends NorthMR_XML_Reader_JavaSaxImpl {
  override protected def parseAndSave(input: InputStream, fileWriter: OutputStreamWriter): Unit = {
    val saxparser = ThreadValueFactory.saxparser
    val processor = if (vender == "zte") new MySAXHandler_empty(fileWriter) with NorthMR_XML_Info_ZTE
    else new MySAXHandler_empty(fileWriter) with NorthMR_XML_Info
    saxparser.parse(input, processor)
  }

  override protected def getProcessor(xmlFileName: String, fileWriter: OutputStreamWriter) =
    new SaxProcessInThread_Empty(xmlFileName, fileWriter)
}



