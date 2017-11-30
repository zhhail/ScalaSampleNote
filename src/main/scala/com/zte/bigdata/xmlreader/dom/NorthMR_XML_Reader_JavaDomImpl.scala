package com.zte.bigdata.xmlreader.dom

import java.io.{FileInputStream, FileOutputStream, OutputStreamWriter}
import java.util.zip.GZIPInputStream
import javax.xml.parsers.{DocumentBuilder, DocumentBuilderFactory}

import com.zte.bigdata.xmlreader.common.{NorthMR_XML_Info, NorthMR_XML_Reader, Using}
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.w3c.dom.{Document, Element, NodeList}

trait NorthMR_XML_Reader_JavaDomImpl extends Using with NorthMR_XML_Reader{
  this: NorthMR_XML_Info =>
  val builderFactory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()

  def writeToFile(data: String, outputFile: String): Unit = {
    using(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8")) {
      fileWriter => fileWriter.write(data)
    }
  }

  override def parseAndSave_gz(xmlFileName: Vector[String], outFileName: String): Unit =
    writeToFile(xml2csv(xmlFileName.head).mkString(",\n") + ",\n", outFileName)

  def parse(xmlFileName: String): Document = {
    val builder: DocumentBuilder = builderFactory.newDocumentBuilder()
    using(new FileInputStream(xmlFileName)) {
      gz => using(new GZIPInputStream(gz))(builder.parse)
    }
  }

  def xml2csv(xmlFileName: String): Seq[String] = {
    var result = List[String]()
    val document: Document = parse(xmlFileName)
    val rootElement: Element = document.getDocumentElement
    val nodes: NodeList = rootElement.getElementsByTagName("eNB")
    for (i <- Range(0, nodes.getLength)) {
      // per eNB
      val eNBId = nodes.item(i).getAttributes.getNamedItem(eNBIdInXml).getNodeValue
      val measurement_node = nodes.item(i).getChildNodes
      for (j <- Range(0, measurement_node.getLength)) {
        // per measurement
        val node = measurement_node.item(j)
        if (node.getNodeName == "measurement") {
          val smrobjlist = node.getChildNodes
          var filterColumsIndex: Vector[Option[Int]] = Vector()
          for (k <- Range(0, smrobjlist.getLength)) {
            val smrobj = smrobjlist.item(k)
            if (smrobj.getNodeName == "smr") {
              val msr = smrobj.getTextContent.toLowerCase.split(" ").zipWithIndex.toMap
              filterColumsIndex = filterColums.map(x=>msr.get(x.toLowerCase))
            }
            else if (filterColumsIndex.count(_.isDefined) > 0 && smrobj.getNodeName == "object") {
              val headinfo = headInfoFromObject.map(key => smrobj.getAttributes.getNamedItem(key).getNodeValue.replaceAll("T", " ")).mkString(",")
              val vs = smrobj.getChildNodes
              val v = Range(0, vs.getLength)
                .map(vs.item)
                .filter(_.getNodeName == "v")
                .map(_.getTextContent.replaceAll("NIL", "").split(" ", -1))
                .map(x => filterColumsIndex.map {
                case Some(i) => x(i)
                case None => ""
              })

              def xadd(s: Vector[String], t: Vector[String]): Vector[String] = {
                s.zip(t).zip(filterColums).map(x => if (x._2.contains("ltenc")) x._1._1 + "$" + x._1._2 else x._1._1)
              }
              result = result :+ s"$eNBId,$headinfo,${v.reduce(xadd).mkString(",")}"
            }
          }
        }
      }
    }
    result
  }
}
