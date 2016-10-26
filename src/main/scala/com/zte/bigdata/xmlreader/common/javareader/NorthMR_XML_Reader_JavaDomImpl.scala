package com.zte.bigdata.xmlreader.common.javareader

import javax.xml.parsers.{DocumentBuilder, DocumentBuilderFactory}
import org.w3c.dom.{NodeList, Element, Document}
import java.io.{FileOutputStream, OutputStreamWriter, File}
import com.zte.bigdata.xmlreader.common.{NorthMR_XML_Reader, NorthMR_XML_Info, Using}

trait NorthMR_XML_Reader_JavaDomImpl extends Using with NorthMR_XML_Reader{
  this: NorthMR_XML_Info =>
  val builderFactory = DocumentBuilderFactory.newInstance()

  def writeToFile(data: String, outputFile: String): Unit = {
    using(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8")) {
      fileWriter => fileWriter.write(data)
    }
  }

  override def parseAndSave(xmlFileName: String, outFileName: String): Unit =
    writeToFile(xml2csv(xmlFileName).mkString(",\n") + ",\n", outFileName)

  def parse(xmlFileName: String): Document = {
    val builder: DocumentBuilder = builderFactory.newDocumentBuilder()
    builder.parse(new File(xmlFileName))
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
              val msr = smrobj.getTextContent.split(" ").zipWithIndex.toMap
              filterColumsIndex = filterColums.map(msr.get)
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
                s.zip(t).zip(filterColums).map(x => if (x._2.contains("LteNc")) x._1._1 + "$" + x._1._2 else x._1._1)
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
