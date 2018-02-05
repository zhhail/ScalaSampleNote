package com.zte.bigdata.xmlreader.common

import java.io.{FileInputStream, FileOutputStream, InputStream, OutputStreamWriter}
import java.util.zip.GZIPInputStream

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream

trait NorthMR_XML_Reader extends Using {
  protected var vender = "NSN"

  def parseAndSave(xmlgzFileNames: Vector[String], outFileName: String): Unit = {
    //    接口中不再支持解析 xml 格式文件，只保留gz后的xml文件处理
    parseAndSave_gz(xmlgzFileNames.filter(_.endsWith(".gz")), outFileName)
    xmlgzFileNames.filter(_.endsWith(".tgz")).par.foreach(x=>parseAndSave(x,outFileName))
  }

  def parseAndSave(inputTargz: String, outFileName: String): Unit = {
    withVender(inputTargz.split("/").last)
    using(new OutputStreamWriter(new FileOutputStream(outFileName), "UTF-8")) {
      fileWriter =>
        using(new FileInputStream(inputTargz)) {
          fin =>
            using(new GzipCompressorInputStream(fin)) {
              inputStream =>
                using(new TarArchiveInputStream(inputStream, "UTF-8")) {
                  tarInput =>
                    while (tarInput.canReadEntryData(tarInput.getNextTarEntry)) processOneTar(tarInput, fileWriter)
                }
            }
        }
    }
  }

  private def processOneTar(tarInput: TarArchiveInputStream, fileWriter: OutputStreamWriter): Unit = {
    val entry = tarInput.getCurrentEntry
    if (!entry.isDirectory) {
      val size = entry.getSize
      if (size > Int.MaxValue) throw new Exception(s"tar size too long, $size > ${Int.MaxValue}")
      val context = new Array[Byte](size.toInt)
      var offset = 0
      while (tarInput.available() > 0) {
        offset += tarInput.read(context, offset, 40960)
      }
      using(new GZIPInputStream(new java.io.ByteArrayInputStream(context))) {
        xml =>
          if (entry.getName.contains("MRO")) parseAndSave(xml, fileWriter)
      }
    }
  }

  private def withVender(vender: String): Unit = this.vender = vender

  protected def parseAndSave(input: InputStream, fileWriter: OutputStreamWriter): Unit

  protected def parseAndSave_gz(xmlgzFileNames: Vector[String], outFileName: String): Unit

}
