package com.zte.bigdata.xmlreader.javareader

import java.io.FileInputStream
import java.util.zip.GZIPInputStream

import com.zte.bigdata.common.{TestUtils, UnitSpec, Using}
import com.zte.bigdata.xmlreader.common.NorthMR_XML_Info
import com.zte.bigdata.xmlreader.sax.{NorthMR_XML_Reader_JavaSaxImpl, NorthMR_XML_Reader_JavaSaxImpl_empty}
import com.zte.bigdata.xmlreader.stax.{NorthMR_XML_Reader_JavaStAXImpl, NorthMR_XML_Reader_JavaStAXImpl_empty}
import org.apache.commons.compress.archivers.{ArchiveEntry, ArchiveInputStream, ArchiveStreamFactory}
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream

class TarGzProcessTest extends UnitSpec with Using with TestUtils{
  describe("gz格式文件测试") {
    it("test gz file") {
      showgz("src/test/resources/gz/FDD_LTE_MRS_HUAWEI_639168_20160817000000.xml.gz")
    }

    it("test tgz gz file") {
      shougztgz("src/test/resources/tgz/all.tgz")
    }
    it ("test performance" ){
      val stax = new NorthMR_XML_Reader_JavaStAXImpl with NorthMR_XML_Info
      val stax_empty = new NorthMR_XML_Reader_JavaStAXImpl_empty with NorthMR_XML_Info {}
      val sax = new NorthMR_XML_Reader_JavaSaxImpl {}
      val sax_empty = new NorthMR_XML_Reader_JavaSaxImpl_empty {}
      val filename = Vector("src/test/resources/gz/FDD_LTE_MRO_HUAWEI_639168_20160817014500.xml.gz")

      val runtimes = 100
      time(runtimes, "sax 空跑")(sax_empty.parseAndSave(filename, "out/stax_hw.csv"))
      time(runtimes, "stax 空跑")(stax_empty.parseAndSave(filename, "out/stax_hw.csv"))
      time(runtimes, "sax 空跑")(sax_empty.parseAndSave(filename, "out/stax_hw.csv"))
      time(runtimes, "stax 空跑")(stax_empty.parseAndSave(filename, "out/stax_hw.csv"))
      time(runtimes, "sax")(sax.parseAndSave(filename, "out/stax_hw.csv"))
      time(runtimes, "stax")(stax.parseAndSave(filename, "out/stax_hw.csv"))
      time(runtimes, "sax")(sax.parseAndSave(filename, "out/stax_hw.csv"))
      time(runtimes, "stax")(stax.parseAndSave(filename, "out/stax_hw.csv"))
    }
  }

  def shougztgz(fileName: String): Unit = {
    val inputStream = new GzipCompressorInputStream(new FileInputStream(fileName))
    val tarInput = new ArchiveStreamFactory().createArchiveInputStream(ArchiveStreamFactory.TAR, inputStream)
    //    val tarInput: TarArchiveInputStream = new TarArchiveInputStream(inputStream)
    processTar(tarInput)
  }

  def processTar(tarInput: ArchiveInputStream): Unit = {
    var entry = tarInput.getNextEntry
    while (tarInput.available() > 0) {
      processOneGzInTar(tarInput, entry)
      entry = tarInput.getNextEntry
    }
  }

  def processOneGzInTar(tarInput: ArchiveInputStream, entry: ArchiveEntry): Unit = {
    val size = entry.getSize.toInt
    val context = new Array[Byte](size)
    var offset = 0
    while (tarInput.available() > 0) {
      offset += tarInput.read(context, offset, 40960)
    }
    val is = new java.io.ByteArrayInputStream(context)
    //    val xml = new GzipCompressorInputStream(is,true)
    //    val xml = new ArchiveStreamFactory().createArchiveInputStream("tar",is)
    val xml = new GZIPInputStream(is)
    show(xml)
    println(s"-- ${entry.getName}")
  }

  def showgz(fileName: String): Unit = {
    using(new FileInputStream(fileName)) {
      gz =>
        using(new GZIPInputStream(gz, 512)) {
          input => show(input)
        }
    }
    println(s"-- $fileName")
  }

  def show(input: GZIPInputStream): Unit = {
    val context = new Array[Byte](40960)
    var size = 0
    while (input.available() > 0) {
      val rl = input.read(context)
      size += new String(context.take(rl), "utf-8").length
    }
    print(f"size: $size%10d --")
  }

  def show(data: Array[Byte]): Unit = {
    print(new String(data, "utf-8"))
  }
}
