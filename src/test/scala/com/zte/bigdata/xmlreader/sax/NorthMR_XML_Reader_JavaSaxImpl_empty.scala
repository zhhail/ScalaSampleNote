package com.zte.bigdata.xmlreader.sax

import java.io.{FileOutputStream, OutputStreamWriter}

import com.zte.bigdata.xmlreader.JavaXmlSaxBoot
import com.zte.bigdata.xmlreader.common.{NorthMR_XML_Reader, Using}
import com.zte.bigdata.xmlreader.sax.common.{SaxProcessInThread, SaxProcessInThread_Empty}


trait NorthMR_XML_Reader_JavaSaxImpl_empty extends NorthMR_XML_Reader_JavaSaxImpl {
  //  override protected def parseAndSave_gz(xmlFileNames: Vector[String], outFileName: String): Unit =
  //    parseAndSave_gz_template(xmlFileNames, outFileName)
  override protected def getProcessor(xmlFileName: String, fileWriter: OutputStreamWriter) =
  new SaxProcessInThread_Empty(xmlFileName, fileWriter)

}



