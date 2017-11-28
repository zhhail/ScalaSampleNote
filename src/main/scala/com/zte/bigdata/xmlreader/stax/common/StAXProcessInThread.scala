package com.zte.bigdata.xmlreader.stax.common

import java.io.{FileInputStream, OutputStreamWriter}
import java.text.SimpleDateFormat
import java.util.zip.GZIPInputStream
import javax.xml.namespace.QName
import javax.xml.stream.{XMLEventReader, XMLInputFactory}

import com.zte.bigdata.common.ThreadValueFactory
import com.zte.bigdata.xmlreader.JavaXmlStAXBoot
import com.zte.bigdata.xmlreader.common.{NorthMR_XML_Info, Using}


class StAXProcessInThread(xmlgzFileName: String, fileWriter: OutputStreamWriter) extends Runnable with Using {
  this: NorthMR_XML_Info =>

  override def run(): Unit = using(new FileInputStream(xmlgzFileName)) {
    gz =>
      using(new GZIPInputStream(gz, 4096)) {
        xml =>
          // Setup a new eventReader
          val inputFactory: XMLInputFactory = ThreadValueFactory.inputFactory
          using(inputFactory.createXMLEventReader(xml))(parser(_, fileWriter))
      }
  }

  def parser(eventReader: XMLEventReader, fileWriter: OutputStreamWriter) {
    var eNBId = ""
    var head = ""
    var objContext = Vector[String]()
    val sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    var indexs: Map[String, Int] = Map()
    var valid: Boolean = indexs.get("MR.LteNcEarfcn").isDefined
    var filterColumsIndex = Vector[Int]()

    // Read the XML document
    while (eventReader.hasNext) {
      val event = eventReader.nextEvent()
      if (event.isStartElement) {
        val startElement = event.asStartElement()
        startElement.getName.getLocalPart match {
          case "v" if valid =>
            addcontext(eventReader.nextEvent().asCharacters().getData)
          case "object" if valid =>
            val h = headInfoFromObject.map(x => startElement.getAttributeByName(new QName(x)).getValue)
            val time = (sDateFormat.parse(h.head.take(10) + " " + h.head.drop(11)).getTime - JavaXmlStAXBoot.dStart) / 1000
            val ms = h.head.takeRight(3).dropWhile(_ == '0')
            head = (Vector(time, ms) ++ h.tail).mkString(",")
          case "eNB" =>
            eNBId = startElement.getAttributeByName(new javax.xml.namespace.QName(eNBIdInXml)).getValue
          case "smr" =>
            val body = eventReader.nextEvent().asCharacters().getData
            indexs = body.split(" ").zipWithIndex.toMap
            valid = indexs.isDefinedAt("MR.LteNcEarfcn")
            filterColumsIndex = filterColums.map(x => indexs.getOrElse(x, 65535))
          case _ =>
        }
      }
      // If we reach the end of an item element we add it to the list
      else if (event.isEndElement) {
        if (event.asEndElement.getName.getLocalPart.equals("object")) {
          if (valid) {
            outputObjectInfo(fileWriter, s"$eNBId,$head,${objContext.mkString(",")},\n")
            objContext = Vector()
          }
        }
      }
    }

    def addcontext(context: String): Unit = {
      val tmp = context.split(" ", -1).toVector
      val filterContext = filterColumsIndex.map {
        case 65535 => ""
        case i =>
          val t = tmp(i)
          if (t == "NIL") "" else t
      }
      if (objContext.isEmpty) objContext = filterContext
      else objContext = xadd(objContext, filterContext)
    }

    def xadd(s: Vector[String], t: Vector[String]): Vector[String] = {
      s.zip(t).zip(filterColums).map {
        case (v, k) => if (k.contains("LteNc")) v._1 + "$" + v._2 else v._1
      }
    }
  }
}

class StAXProcessInThread_Empty(xmlgzFileName: String, fileWriter: OutputStreamWriter) extends StAXProcessInThread(xmlgzFileName, fileWriter) {
  this: NorthMR_XML_Info =>
  override def parser(eventReader: XMLEventReader, fileWriter: OutputStreamWriter) {
    // Read the XML document
    while (eventReader.hasNext) {
      //      eventReader.nextEvent()
      eventReader.next()
    }
  }

}