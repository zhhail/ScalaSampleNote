package com.zte.bigdata.xmlreader.stax

import java.io.{FileOutputStream, InputStream, OutputStreamWriter}
import javax.xml.stream.XMLInputFactory

import com.zte.bigdata.common.ThreadValueFactory
import com.zte.bigdata.xmlreader.JavaXmlStAXBoot
import com.zte.bigdata.xmlreader.common._
import com.zte.bigdata.xmlreader.stax.common.StAXProcessInThread


trait NorthMR_XML_Reader_JavaStAXImpl extends NorthMR_XML_Reader with Using with WithMultiThread {
  override protected def parseAndSave(input: InputStream, fileWriter: OutputStreamWriter): Unit = {
    val tmp = new StAXProcessInThread("", null) with NorthMR_XML_Reader_JavaStAXImpl with NorthMR_XML_Info {}
    val inputFactory: XMLInputFactory = ThreadValueFactory.inputFactory
    using(inputFactory.createXMLEventReader(input))(tmp.parser(_, fileWriter))
  }
  import java.util.concurrent.{Executors, TimeUnit}
  override protected def parseAndSave_gz(xmlgzFileNames: Vector[String], outFileName: String): Unit = {
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
            fixedThreadPool.shutdown()
            fixedThreadPool.awaitTermination(10, TimeUnit.MINUTES)
        }
//    using(new OutputStreamWriter(new FileOutputStream(outFileName), "UTF-8")) {
//      fileWriter =>
//        val processors = xmlgzFileNames.map { xmlgzFileName =>
//          xmlgzFileName.split("/").last match {
//            case zte: String if zte.startsWith("FDD-LTE_MRO_ZTE") => getStAXProcess(zte = true, xmlgzFileName, fileWriter)
//            case _ => getStAXProcess(zte = false, xmlgzFileName, fileWriter)
//          }
//        }
//        withMultiThread(JavaXmlStAXBoot.threadNum)(processors)
//    }

  }

  protected def getStAXProcess(zte: Boolean, xmlgzFileName: String, fileWriter: OutputStreamWriter): StAXProcessInThread with NorthMR_XML_Info = {
    if (zte) new StAXProcessInThread(xmlgzFileName, fileWriter) with NorthMR_XML_Info_ZTE
    else new StAXProcessInThread(xmlgzFileName, fileWriter) with NorthMR_XML_Info
  }
}

