package com.zte.bigdata.xmlreader.common

import java.io.{FileInputStream, FileOutputStream, InputStream, OutputStreamWriter}
import java.util.concurrent.Executors
import java.util.zip.GZIPInputStream

import com.zte.bigdata.common.ThreadValueFactory
import com.zte.bigdata.xmlreader.sax.common.MySAXHandler
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream

trait NorthMR_XML_Info {

  val filterColums: Vector[String] = Vector("MR.LTESCEARFCN",
    "MR.LTESCPCI",
    "MR.LTESCRSRP",
    "MR.LTESCRSRQ",
    "MR.LTESCTADV",
    "MR.LTESCPHR",
    "MR.LTESCAOA",
    "MR.LTESCSINRUL",
    "MR.LTESCRI1",
    "MR.LTESCRI2",
    "MR.LTESCRI4",
    "MR.LTESCRI8",
    "MR.CQI0",
    "MR.CQI1",
    "MR.LATITUDESIGN",
    "MR.LTENCEARFCN",
    "MR.LTENCPCI",
    "MR.LTENCRSRP",
    "MR.LTENCRSRQ",
    "MR.LTESCPUSCHPRBNUM",
    "MR.LTESCPDSCHPRBNUM",
    "MR.LTESCBSR",
    "MR.CDMATYPE",
    "MR.CDMANCBAND",
    "MR.CDMANCARFCN",
    "MR.CDMAPNPHASE",
    "MR.LTECDMAORHRPDNCPILOTSTRENGTH",
    "MR.CDMANCPCI",
    "MR.LONGITUDE",
    "MR.LATITUDE").map(_.toLowerCase)

  val headInfoFromObject = Vector(
    "TimeStamp",
    "MmeUeS1apId",
    "MmeGroupId",
    "MmeCode",
    "id")

  def outputObjectInfo(fileWriter: OutputStreamWriter, line: String): Unit = {
    fileWriter.write(line)
  }
  val eNBIdInXml = "id"
}


class NorthMR_XML_Reader_Thread(tarInput: TarArchiveInputStream,fileWriter: OutputStreamWriter) extends Runnable with Using{
  override def run() = {

    val entry = tarInput.getCurrentEntry
    if (!entry.isDirectory) {
      val size = entry.getSize.toInt
      val context = new Array[Byte](size)
      var offset = 0
      while (tarInput.available() > 0) {
        offset += tarInput.read(context, offset, 40960)
      }
      using(new GZIPInputStream(new java.io.ByteArrayInputStream(context.take(offset)))){
        xml =>
          //          processxml(xml, fileWriter)
          //          println(s"-- ${entry.getName}")
          //          if (entry.getName.contains("MRO")) processxml(xml, fileWriter)
          //          println(s"$offset -- ${entry.getName}")
          if (entry.getName.contains("MRO")) parseAndSave(xml, fileWriter)
      }
    }
  }

  def parseAndSave(input: InputStream, fileWriter: OutputStreamWriter):Unit =  {
    val saxparser = ThreadValueFactory.saxparser
    val processor = new MySAXHandler(fileWriter) with NorthMR_XML_Info
    saxparser.parse(input, processor)
  }

}
trait NorthMR_XML_Reader extends Using {
  private var vender = "NSN"

  def parseAndSave(inputTargz: String, outFileName: String, vender: String = "NSN"): Unit = {
    val fixedThreadPool = Executors.newFixedThreadPool(4)

    using(new OutputStreamWriter(new FileOutputStream(outFileName), "UTF-8")) {
      fileWriter =>
      using(new FileInputStream(inputTargz)) {
        fin =>
          using(new GzipCompressorInputStream(fin)) {
            inputStream =>
              using(new TarArchiveInputStream(inputStream)) {
                tarInput =>
//                  while (tarInput.canReadEntryData(tarInput.getNextTarEntry)) fixedThreadPool.execute(new NorthMR_XML_Reader_Thread(tarInput,fileWriter))
                  while (tarInput.canReadEntryData(tarInput.getNextTarEntry)) processOneTar(tarInput,fileWriter)
              }
          }
      }
    }
  }

  private def processOneTar(tarInput: TarArchiveInputStream,fileWriter: OutputStreamWriter): Unit = {

    val entry = tarInput.getCurrentEntry
    if (!entry.isDirectory) {
      val size = entry.getSize.toInt
      val context = new Array[Byte](size)
      var offset = 0
      while (tarInput.available() > 0) {
        offset += tarInput.read(context, offset, 40960)
      }
      using(new GZIPInputStream(new java.io.ByteArrayInputStream(context))){
        xml =>
          if (entry.getName.contains("MRO")) parseAndSave(xml, fileWriter)
      }
    }
  }

  def parseAndSave(input: InputStream, fileWriter: OutputStreamWriter):Unit =  {
    val saxparser = ThreadValueFactory.saxparser
    val processor = new MySAXHandler(fileWriter) with NorthMR_XML_Info
    saxparser.parse(input, processor)
  }


  private def processxml(input: InputStream, fileWriter: OutputStreamWriter):Unit = {
    val context = new Array[Byte](40960)
    var size = 0
    while (input.available() > 0) {
      val rl = input.read(context)
      size += new String(context.take(rl), "utf-8").length
    }
    print(f"size: $size%10d --")
  }

  def withVender(vender: String): Unit = this.vender = vender

  def parseAndSave(xmlgzFileNames: Vector[String], outFileName: String): Unit = {
    //    接口中不再支持解析 xml 格式文件，只保留gz后的xml文件处理
    parseAndSave_gz(xmlgzFileNames.filter(_.endsWith(".gz")), outFileName)
  }

  protected def parseAndSave_gz(xmlgzFileNames: Vector[String], outFileName: String): Unit

}

trait NorthMR_XML_Info_ZTE extends NorthMR_XML_Info {
  override val eNBIdInXml = "MR.eNBId"
  override val headInfoFromObject = Vector(
    "MR.TimeStamp",
    "MR.MmeUeS1apId",
    "MR.MmeGroupId",
    "MR.MmeCode",
    "MR.objectId")
}
