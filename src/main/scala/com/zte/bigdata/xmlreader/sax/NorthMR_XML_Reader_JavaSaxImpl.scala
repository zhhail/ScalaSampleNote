package com.zte.bigdata.xmlreader.sax

import java.io.{FileOutputStream, InputStream, OutputStreamWriter}

import com.zte.bigdata.common.ThreadValueFactory
import com.zte.bigdata.xmlreader.JavaXmlSaxBoot
import com.zte.bigdata.xmlreader.common._
import com.zte.bigdata.xmlreader.sax.common.{MySAXHandler, SaxProcessInThread}


trait NorthMR_XML_Reader_JavaSaxImpl extends NorthMR_XML_Reader with Using with WithMultiThread {
  override protected def parseAndSave(input: InputStream, fileWriter: OutputStreamWriter): Unit = {
    val saxparser = ThreadValueFactory.saxparser
    val processor = if (vender == "zte") new MySAXHandler(fileWriter) with NorthMR_XML_Info_ZTE
    else new MySAXHandler(fileWriter) with NorthMR_XML_Info
    saxparser.parse(input, processor)
  }
  override protected def parseAndSave_gz(xmlFileNames: Vector[String], outFileName: String): Unit = {
    using(new OutputStreamWriter(new FileOutputStream(outFileName), "UTF-8")) {
      fileWriter => withMultiThread(JavaXmlSaxBoot.threadNum)(xmlFileNames.map(x=>getProcessor(x, fileWriter)))
    }
  }

  protected def getProcessor(xmlFileName: String, fileWriter: OutputStreamWriter) =
    new SaxProcessInThread(xmlFileName, fileWriter)
}





