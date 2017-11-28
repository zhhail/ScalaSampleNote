package com.zte.bigdata.xmlreader.stax

import java.io.{FileOutputStream, OutputStreamWriter}

import com.zte.bigdata.xmlreader.JavaXmlStAXBoot
import com.zte.bigdata.xmlreader.common.{NorthMR_XML_Info, NorthMR_XML_Info_ZTE, NorthMR_XML_Reader, Using}
import com.zte.bigdata.xmlreader.stax.common.{StAXProcessInThread, StAXProcessInThread_Empty}


trait NorthMR_XML_Reader_JavaStAXImpl extends NorthMR_XML_Reader with Using {
  override protected def parseAndSave_gz(xmlgzFileNames: Vector[String], outFileName: String): Unit = {
    import java.util.concurrent.{Executors, TimeUnit}
    val fixedThreadPool = Executors.newFixedThreadPool(JavaXmlStAXBoot.threadNum)
    using(new OutputStreamWriter(new FileOutputStream(outFileName), "UTF-8")) {
      fileWriter =>
        xmlgzFileNames.foreach {
          xmlgzFileName =>
            val processor = xmlgzFileName.split("/").last match {
              case zte: String if zte.startsWith("FDD-LTE_MRO_ZTE") => getStAXProcess(zte = true, xmlgzFileName, fileWriter)
              case _ => getStAXProcess(zte = false, xmlgzFileName, fileWriter)
            }
            fixedThreadPool.execute(processor)
        }
    }
    fixedThreadPool.shutdown()
    fixedThreadPool.awaitTermination(10, TimeUnit.MINUTES)
  }

  protected def getStAXProcess(zte: Boolean, xmlgzFileName: String, fileWriter: OutputStreamWriter): StAXProcessInThread with NorthMR_XML_Info = {
    if (zte) new StAXProcessInThread(xmlgzFileName, fileWriter) with NorthMR_XML_Info_ZTE
    else new StAXProcessInThread(xmlgzFileName, fileWriter) with NorthMR_XML_Info
  }
}

trait NorthMR_XML_Reader_JavaStAXImpl_empty extends NorthMR_XML_Reader_JavaStAXImpl {
  override protected def getStAXProcess(zte: Boolean, xmlgzFileName: String, fileWriter: OutputStreamWriter): StAXProcessInThread with NorthMR_XML_Info = {
    if (zte) new StAXProcessInThread_Empty(xmlgzFileName, fileWriter) with NorthMR_XML_Info_ZTE
    else new StAXProcessInThread_Empty(xmlgzFileName, fileWriter) with NorthMR_XML_Info
  }

}
