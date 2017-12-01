package com.zte.bigdata.xmlreader.javareader

import java.io.{FileInputStream, InputStream, OutputStreamWriter}
import java.util.zip.GZIPInputStream
import javax.xml.stream.XMLInputFactory

import com.zte.bigdata.common.{TestUtils, ThreadValueFactory, UnitSpec, Using}
import com.zte.bigdata.xmlreader.common.{NorthMR_XML_Info, NorthMR_XML_Reader}
import com.zte.bigdata.xmlreader.sax.{NorthMR_XML_Reader_JavaSaxImpl, NorthMR_XML_Reader_JavaSaxImpl_empty}
import com.zte.bigdata.xmlreader.stax.common.StAXProcessInThread
import com.zte.bigdata.xmlreader.stax.{NorthMR_XML_Reader_JavaStAXImpl, NorthMR_XML_Reader_JavaStAXImpl_empty}
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream

class TarGzProcessTest extends UnitSpec with Using with TestUtils{
  describe("等价性测试： sax == stax") {
    pending
    it("test HW MRO： sax == stax ") {
      val inputFile = "src/test/resources/gz/FDD-LTE_MRO_HUAWEI_86493_20170902043000.xml.gz"
      val outSax = "out/difftest_sax_hw_gz.csv"
      val outStAX = "out/difftest_stax_hw_gz.csv"

      val sax = new NorthMR_XML_Reader_JavaSaxImpl with NorthMR_XML_Info
      val stax = new NorthMR_XML_Reader_JavaStAXImpl with NorthMR_XML_Info
      sax.parseAndSave(Vector(inputFile), outSax)
      stax.parseAndSave(Vector(inputFile), outStAX)
      val filesax = scala.io.Source.fromFile(outSax).getLines().toList
      val filestax = scala.io.Source.fromFile(outStAX).getLines().toList
      filesax shouldBe filestax
    }
  }

  describe("sax stax 性能对比测试") {
    pending
    it("gz - HW MRO : sax vs stax") {
      val stax = new NorthMR_XML_Reader_JavaStAXImpl with NorthMR_XML_Info {
        override def outputObjectInfo(fileWriter: OutputStreamWriter, line: String): Unit = {}
      }
      val stax_empty = new NorthMR_XML_Reader_JavaStAXImpl_empty with NorthMR_XML_Info {
        override def outputObjectInfo(fileWriter: OutputStreamWriter, line: String): Unit = {}
      }
      val sax = new NorthMR_XML_Reader_JavaSaxImpl {}
      val sax_empty = new NorthMR_XML_Reader_JavaSaxImpl_empty {}
      val filename = Vector("src/test/resources/gz/FDD-LTE_MRO_HUAWEI_86493_20170902043000.xml.gz")

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

  describe("gz targz 大压缩包功能测试") {
    it("test tgz gz file") {
      pending
      val file_test = "src/test/resources/tgz/all.tgz"
      val file_real = "src/test/resources/tgz/HW_HN_OMC1-mr-134.175.57.16-20170921043000-20170921044500-20170921051502-001.tar.gz"
      processFilanTgzgz(file_test)
      processFilanTgzgz(file_real)
    }
  }
  describe("gz targz 大压缩包测试") {
    val times = 1
    it("test sax tgz gz file") {
      val file_test = "src/test/resources/tgz/all.tgz"
      val file_real = "src/test/resources/tgz/HW_HN_OMC1-mr-134.175.57.16-20170921043000-20170921044500-20170921051502-001.tar.gz"
      val tmp = new NorthMR_XML_Reader {
        override protected def parseAndSave_gz(xmlgzFileNames: Vector[String], outFileName: String): Unit = ???
      }
      time("all tgz -- test file",times)(tmp.parseAndSave(file_test, "out/all-tgz-sax-test.csv"))
      time("all tgz -- real file",times)(tmp.parseAndSave(file_real, "out/all-tgz-sax-real.csv"))
    }
    it("test stax tgz gz file") {
      val file_test = "src/test/resources/tgz/all.tgz"
      val file_real = "src/test/resources/tgz/HW_HN_OMC1-mr-134.175.57.16-20170921043000-20170921044500-20170921051502-001.tar.gz"
      val tmp = new StAXProcessInThread("", null) with NorthMR_XML_Reader with NorthMR_XML_Info {
        override protected def parseAndSave_gz(xmlgzFileNames: Vector[String], outFileName: String): Unit = ???

        override def parseAndSave(input: InputStream, fileWriter: OutputStreamWriter): Unit = {
          val inputFactory: XMLInputFactory = ThreadValueFactory.inputFactory
          using(inputFactory.createXMLEventReader(input))(parser(_, fileWriter))
        }
      }
      time("all tgz -- test file",times)(tmp.parseAndSave(file_test, "out/all-tgz-stax-test.csv"))
      time("all tgz -- real file",times)(tmp.parseAndSave(file_real, "out/all-tgz-stax-real.csv"))
    }
  }

  def processFilanTgzgz(fileName: String): Unit = {
    // 功能研究，暂未考虑文件句柄释放问题
    val fin = new FileInputStream(fileName)
    val inputStream = new GzipCompressorInputStream(fin)
    //    val tarInput = new ArchiveStreamFactory().createArchiveInputStream(ArchiveStreamFactory.TAR, inputStream)
    val tarInput: TarArchiveInputStream = new TarArchiveInputStream(inputStream)
    while (tarInput.canReadEntryData(tarInput.getNextEntry)) processOneGz(tarInput)
  }

  def processOneGz(tarInput: TarArchiveInputStream): Unit = {
    val entry = tarInput.getCurrentEntry
    if (entry.isFile && entry.getName.contains("MRO")) {
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
