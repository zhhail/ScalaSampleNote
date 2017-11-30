package com.zte.bigdata.xmlreader.sax.common

import java.io.OutputStreamWriter
import java.text.SimpleDateFormat

import com.zte.bigdata.xmlreader.JavaXmlSaxBoot
import com.zte.bigdata.xmlreader.common.NorthMR_XML_Info
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler

class MySAXHandler(fileWriter: OutputStreamWriter) extends DefaultHandler {
  this: NorthMR_XML_Info =>
  private val sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  private var ename = ""
  private var eNBId = ""
  private var head = ""
  private var objContext = Vector[String]()
  private var smr = Vector[String]()
  private var indexs: Map[String, Int] = smr.zipWithIndex.toMap
  private var valid: Boolean = indexs.get("MR.LteNcEarfcn".toLowerCase).isDefined
  private var filterColumsIndex = Vector[Int]()

  override def startElement(uri: String,
                            localName: String,
                            qName: String,
                            attributes: Attributes): Unit = {
    ename = qName
    qName match {
      case "eNB" =>
        eNBId = attributes.getValue(eNBIdInXml)
      case "object" if valid =>
        val h = headInfoFromObject.map(attributes.getValue).map(x=>if(x=="NIL")"" else x)
        val time = (sDateFormat.parse(h.head.take(10) + " " + h.head.drop(11)).getTime - JavaXmlSaxBoot.dStart) / 1000
        val ms = h.head.takeRight(3).dropWhile(_ == '0')
        head = (Vector(time, ms) ++ h.tail).mkString(",")
      case _ =>
    }
  }

  override def endElement(uri: String,
                          localName: String,
                          qName: String): Unit = {
    ename = ""
    qName match {
      case "measurement" =>
        smr = Vector()
      case "object" if valid =>
        outputObjectInfo(fileWriter, s"$eNBId,$head,${objContext.mkString(",")},\n")
        objContext = Vector()
      case _ =>
    }
  }

  override def characters(ch: Array[Char], start: Int, length: Int): Unit = {
    ename match {
      case "v" if valid =>
        addcontext(new String(ch, start, length))
      case "smr" =>
        val context = new String(ch, start, length)
        smr = context.toLowerCase.split(" ").toVector
        indexs = smr.zipWithIndex.toMap
        valid = indexs.get("MR.LteNcEarfcn".toLowerCase).isDefined
        filterColumsIndex = filterColums.map(x => indexs.getOrElse(x, 65535))
      case _ =>
    }
  }

  private def addcontext(context: String): Unit = {
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

  private def xadd(s: Vector[String], t: Vector[String]): Vector[String] = {
    s.zip(t).zip(filterColums).map {
      case (v, k) => if (k.contains("ltenc")) v._1 + "$" + v._2 else v._1
    }
  }
}
