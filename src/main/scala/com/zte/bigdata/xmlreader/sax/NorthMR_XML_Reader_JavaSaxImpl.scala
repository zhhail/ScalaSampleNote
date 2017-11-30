package com.zte.bigdata.xmlreader.sax

import java.io.{FileOutputStream, OutputStreamWriter}

import com.zte.bigdata.xmlreader.JavaXmlSaxBoot
import com.zte.bigdata.xmlreader.common.{NorthMR_XML_Reader, Using}
import com.zte.bigdata.xmlreader.sax.common.SaxProcessInThread


trait NorthMR_XML_Reader_JavaSaxImpl extends NorthMR_XML_Reader with Using {

  override protected def parseAndSave_gz(xmlFileNames: Vector[String], outFileName: String): Unit = {
    import java.util.concurrent.{Executors, TimeUnit}
    val fixedThreadPool = Executors.newFixedThreadPool(JavaXmlSaxBoot.threadNum)
    using(new OutputStreamWriter(new FileOutputStream(outFileName), "UTF-8")) {
      fileWriter =>
        xmlFileNames.foreach {
          xmlFileName => fixedThreadPool.execute(getProcessor(xmlFileName, fileWriter))
        }
        fixedThreadPool.shutdown()
        fixedThreadPool.awaitTermination(60, TimeUnit.MINUTES)
    }
  }

  protected def getProcessor(xmlFileName: String, fileWriter: OutputStreamWriter) =
    new SaxProcessInThread(xmlFileName, fileWriter)

  //  private def parseAndSave_xml(xmlFileNames: Vector[String], outFileName: String): Unit = {
  //    using(new OutputStreamWriter(new FileOutputStream(outFileName), "UTF-8")) {
  //      fileWriter =>
  //        xmlFileNames.par.foreach {
  //          xmlFileName =>
  //            val saxparser: SAXParser = ThreadValueFactory.saxparser
  //            val processor = xmlFileName.split("/").last match {
  //              case zte: String if zte.startsWith("FDD-LTE_MRO_ZTE") => new handler(fileWriter) with NorthMR_XML_Info_ZTE
  //              case _ => new handler(fileWriter) with NorthMR_XML_Info
  //            }
  //            using(new FileInputStream(xmlFileName)) { input => saxparser.parse(input, processor) }
  //        }
  //    }
  //  }

}





