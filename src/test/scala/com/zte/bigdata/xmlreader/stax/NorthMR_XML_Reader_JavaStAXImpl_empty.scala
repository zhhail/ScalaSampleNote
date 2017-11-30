package com.zte.bigdata.xmlreader.stax

import java.io.{FileOutputStream, OutputStreamWriter}

import com.zte.bigdata.xmlreader.JavaXmlStAXBoot
import com.zte.bigdata.xmlreader.common.{NorthMR_XML_Info, NorthMR_XML_Info_ZTE, NorthMR_XML_Reader, Using}
import com.zte.bigdata.xmlreader.stax.common.{StAXProcessInThread, StAXProcessInThread_Empty}


trait NorthMR_XML_Reader_JavaStAXImpl_empty extends NorthMR_XML_Reader_JavaStAXImpl {
  override protected def getStAXProcess(zte: Boolean, xmlgzFileName: String, fileWriter: OutputStreamWriter): StAXProcessInThread with NorthMR_XML_Info = {
    if (zte) new StAXProcessInThread_Empty(xmlgzFileName, fileWriter) with NorthMR_XML_Info_ZTE
    else new StAXProcessInThread_Empty(xmlgzFileName, fileWriter) with NorthMR_XML_Info
  }

}
