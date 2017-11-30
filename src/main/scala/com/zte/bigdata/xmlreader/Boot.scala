package com.zte.bigdata.xmlreader
import java.io.File
import java.text.SimpleDateFormat

import com.zte.bigdata.xmlreader.common.{NorthMR_XML_Info, NorthMR_XML_Info_ZTE, NorthMR_XML_Reader}
import com.zte.bigdata.xmlreader.dom.{NorthMR_XML_Reader_JavaDomImpl, NorthMR_XML_Reader_ScalaDomImpl}
import com.zte.bigdata.xmlreader.sax.{NorthMR_XML_Reader_JavaSaxImpl, NorthMR_XML_Reader_ScalaSaxImpl}
import com.zte.bigdata.xmlreader.stax.NorthMR_XML_Reader_JavaStAXImpl


trait XmlBoot {
  def processor(input: Vector[String]): NorthMR_XML_Reader
  val sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  val dStart: Long = sDateFormat.parse("2000-01-01 00:00:00").getTime

  var threadNum = 10
  def main(args: Array[String]): Unit = {
    if (args.length < 2) {
      println(s"usage: Boot inputfile outputfile [threadNum]")
      sys.exit()
    }
    threadNum = scala.util.Try(args(2).toInt).getOrElse(threadNum)
    val t0=System.nanoTime : Double
    val (input, output) = (args(0), args(1))
    val files = new File(input).listFiles().filter(_.isFile).map(_.getName).toVector.map(f=>s"$input/$f")
    val time_listfile = (System.nanoTime-t0)/1000000
    print(f"list file: $time_listfile%4.3f, filenum: ${files.length}, ")
    processor(files).parseAndSave(files, output)
    println(f"total: ${(System.nanoTime-t0)/1000000}%6.3f}, ${collection.parallel.ForkJoinTasks.defaultForkJoinPool.getParallelism}")
  }
}

object ScalaXmlDomBoot extends XmlBoot {
  override def processor(input: Vector[String]): NorthMR_XML_Reader = input.head match {
    case zte: String if zte.startsWith("FDD-LTE_MRO_ZTE") => new NorthMR_XML_Reader_ScalaDomImpl with NorthMR_XML_Info_ZTE
    case _ => new NorthMR_XML_Reader_ScalaDomImpl with NorthMR_XML_Info
  }

}

object ScalaXmlSaxBoot extends XmlBoot {
  override def processor(input: Vector[String]): NorthMR_XML_Reader = input.head match {
    case zte: String if zte.startsWith("FDD-LTE_MRO_ZTE") => new NorthMR_XML_Reader_ScalaSaxImpl with NorthMR_XML_Info_ZTE
    case _ => new NorthMR_XML_Reader_ScalaSaxImpl with NorthMR_XML_Info
  }
}

object JavaXmlDomBoot extends XmlBoot {
  override def processor(input: Vector[String]): NorthMR_XML_Reader = input.head match {
    case zte: String if zte.startsWith("FDD-LTE_MRO_ZTE") => new NorthMR_XML_Reader_JavaDomImpl with NorthMR_XML_Info_ZTE
    case _ => new NorthMR_XML_Reader_JavaDomImpl with NorthMR_XML_Info
  }
}

object JavaXmlSaxBoot extends XmlBoot {
  override def processor(input: Vector[String]): NorthMR_XML_Reader =  new NorthMR_XML_Reader_JavaSaxImpl {}
}

object JavaXmlStAXBoot extends XmlBoot {
  override def processor(input: Vector[String]): NorthMR_XML_Reader =  new NorthMR_XML_Reader_JavaStAXImpl with NorthMR_XML_Info{}
}

