package com.zte.bigdata.xmlreader.common.scalareader

import java.io.{FileOutputStream, OutputStreamWriter}
import scala.xml.Elem
import com.zte.bigdata.xmlreader.common.{Using, NorthMR_XML_Reader, RecordInfo, NorthMR_XML_Info}

/**
 * Created by 10010581 on 2016/10/19.
 */
trait NorthMR_XML_Reader_ScalaImpl extends  NorthMR_XML_Reader  with Using{
  this:NorthMR_XML_Info =>
  def writeToFile(data: String, outputFile: String): Unit = {
    using(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8")) {
      fileWriter => fileWriter.write(data)
    }
  }

  def parseAndSave(xmlFileName: String, outFileName: String): Unit =
    writeToFile(xml2csv(xmlFileName).mkString("\n") + "\n", outFileName)

  def loadfile(xmlFileName: String): Elem

  def xml2csv(xmlFileName: String): Seq[String] = {
    val file: Elem = loadfile(xmlFileName)

    val eNBs = file \ "eNB"
    eNBs.flatMap {
      eNB =>
        val eNBId = (eNB \ ("@" + eNBIdInXml)).text
        // println("@@@@@@@@@@"+eNBId)
        val mr = eNB \ "measurement" filter (m => (m \ "smr").text.contains("MR.LteNcEarfcn"))
        val columsMap = (mr \ "smr").text.split(" ", -1).zipWithIndex.toMap
        val columsIndexs = filterColums.map(x => RecordInfo(columsMap.getOrElse(x, -1), x.contains("LteNc")))
        mr \ "object" flatMap {
          obj =>
            val header = s"$eNBId," + headInfoFromObject.map(c => (obj \\ s"@$c").text).mkString(",").replaceAll("NIL", "").replaceAll("T", " ")
            val value = (obj \ "v").map(_.text.trim.replaceAll("NIL", "").split(" ", -1))
            // val finalValue1 = value.map(line => columsIndexs.map(x=>if(x.index != -1)line(x.index) else ""))
            val tempvalue = value.map(line => columsIndexs.map(x => if (x.index != -1) (line(x.index), x.neighbor) else ("", x.neighbor)))
            val finalValue = Seq(tempvalue.reduce {
              (r, line) =>
                r.zip(line).map(e => if (e._1._2) (e._1._1 + "$" + e._2._1, e._1._2) else e._1)
            }.map(_._1))
            finalValue.map(v => header + "," + v.mkString(",") + ",")
        }
    }
  }
}
