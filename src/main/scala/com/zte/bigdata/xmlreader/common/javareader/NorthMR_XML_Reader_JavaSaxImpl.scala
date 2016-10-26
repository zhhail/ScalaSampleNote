package com.zte.bigdata.xmlreader.common.javareader

import com.zte.bigdata.xmlreader.common.{NorthMR_XML_Info_ZTE, Using, NorthMR_XML_Reader, NorthMR_XML_Info}
import javax.xml.parsers.SAXParserFactory
import java.io.{FileOutputStream, OutputStreamWriter, FileInputStream}
import org.xml.sax.helpers.DefaultHandler
import org.xml.sax.Attributes


trait NorthMR_XML_Reader_JavaSaxImpl extends NorthMR_XML_Reader with Using {

  override def parseAndSave(xmlFileName: String, outFileName: String): Unit = {
    using(new OutputStreamWriter(new FileOutputStream(outFileName), "UTF-8")) {
      fileWriter =>
        val saxfac = SAXParserFactory.newInstance()
        val saxparser = saxfac.newSAXParser()
        val processor = xmlFileName match {
          case zte: String if zte.startsWith("FDD-LTE_MRO_ZTE") => new MySAXHandler(fileWriter) with NorthMR_XML_Info_ZTE
          case _ => new MySAXHandler(fileWriter) with NorthMR_XML_Info
        }
        saxparser.parse(new FileInputStream(xmlFileName), processor)
    }
  }
}

class MySAXHandler(fileWriter: OutputStreamWriter) extends DefaultHandler {
  this: NorthMR_XML_Info =>
  var ename = ""
  var eNBId = ""
  var head = ""
  var objContext = Vector[String]()
  var smr = Vector[String]()
  var filterColumsIndex = Vector[Option[Int]]()

  def xadd(s: Vector[String], t: Vector[String]): Vector[String] = {
    s.zip(t).zip(filterColums).map(x => if (x._2.contains("LteNc")) x._1._1 + "$" + x._1._2 else x._1._1)
  }

  def outputObjectInfo() = {
    fileWriter.write(s"$eNBId,$head,${objContext.mkString(",")},\n")
    objContext = Vector()
  }

  def addcontext(context: String): Unit = {
    val tmp = context.split(" ", -1)
    val filterContext = filterColumsIndex.map {
      case Some(i) => tmp(i)
      case None => ""
    }
    if (objContext.isEmpty) objContext = filterContext
    else objContext = xadd(objContext, filterContext)
  }

  override def startElement(uri: String,
                            localName: String,
                            qName: String,
                            attributes: Attributes) = {
    ename = qName
    qName match {
      case "eNB" =>
        eNBId = attributes.getValue(eNBIdInXml)
      case "object" =>
        head = headInfoFromObject.map(attributes.getValue).mkString(",").replaceAll("T", " ")
      case _ =>
    }
  }

  override def endElement(uri: String,
                          localName: String,
                          qName: String) = {
    ename = ""
    qName match {
      case "measurement" =>
        smr = Vector()
      case "object" =>
        if (smr.contains("MR.LteNcEarfcn")) outputObjectInfo()
      case _ =>
    }
  }

  override def characters(ch: Array[Char], start: Int, length: Int): Unit = {
    val context = new String(ch, start, length)
    ename match {
      case "smr" =>
        smr = context.split(" ").toVector
        filterColumsIndex = filterColums.map(x => smr.zipWithIndex.toMap.get(x))
      case "v" if smr.contains("MR.LteNcEarfcn") =>
        addcontext(context.replaceAll("NIL", ""))
      case _ =>
    }
  }

  val result = ""
}