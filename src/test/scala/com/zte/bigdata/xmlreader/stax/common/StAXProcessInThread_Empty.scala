package com.zte.bigdata.xmlreader.stax.common

import java.io.{FileInputStream, OutputStreamWriter}
import java.text.SimpleDateFormat
import java.util.zip.GZIPInputStream
import javax.xml.namespace.QName
import javax.xml.stream.{XMLEventReader, XMLInputFactory}

import com.zte.bigdata.common.ThreadValueFactory
import com.zte.bigdata.xmlreader.JavaXmlStAXBoot
import com.zte.bigdata.xmlreader.common.{NorthMR_XML_Info, Using}


class StAXProcessInThread_Empty(xmlgzFileName: String, fileWriter: OutputStreamWriter) extends StAXProcessInThread(xmlgzFileName, fileWriter) {
  this: NorthMR_XML_Info =>
  override def parser(eventReader: XMLEventReader, fileWriter: OutputStreamWriter) {
    var meet_mro = false
    var mrodone = false

    // Read the XML document
    while (eventReader.hasNext && !mrodone) {
      val event = eventReader.nextEvent()
      if (event.isStartElement) {
        val startElement = event.asStartElement()
        startElement.getName.getLocalPart match {
          case "smr" =>
            val body = eventReader.nextEvent().asCharacters().getData
            val indexs = body.toLowerCase.split(" ").zipWithIndex.toMap
            meet_mro = indexs.isDefinedAt("MR.LteNcEarfcn".toLowerCase)
          case _ =>
        }
      }
      // If we reach the end of an item element we add it to the list
      else if (event.isEndElement) {
        event.asEndElement.getName.getLocalPart match {
          case "measurement" if meet_mro => mrodone = true
          case _ =>
        }
      }
    }
  }
}