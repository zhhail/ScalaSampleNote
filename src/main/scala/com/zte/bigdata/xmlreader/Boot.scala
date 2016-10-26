package com.zte.bigdata.xmlreader

import com.zte.bigdata.xmlreader.common.{NorthMR_XML_Info_ZTE, NorthMR_XML_Info, NorthMR_XML_Reader}
import com.zte.bigdata.xmlreader.common.scalareader.{NorthMR_XML_Reader_ScalaSaxImpl, NorthMR_XML_Reader_ScalaDomImpl}
import com.zte.bigdata.xmlreader.common.javareader.{NorthMR_XML_Reader_JavaSaxImpl, NorthMR_XML_Reader_JavaDomImpl}

trait XmlBoot {
  def processor(input: String): NorthMR_XML_Reader

  def main(args: Array[String]) = {
    if (args.length < 2) {
      println(s"usage: Dom inputfile outputfile ")
      sys.exit()
    }
    val (input, output) = (args(0), args(1))
    processor(input).parseAndSave(input, output)
  }
}

object ScalaXmlDomBoot extends XmlBoot {
  override def processor(input: String) = input match {
    case zte: String if zte.startsWith("FDD-LTE_MRO_ZTE") => new NorthMR_XML_Reader_ScalaDomImpl with NorthMR_XML_Info_ZTE
    case _ => new NorthMR_XML_Reader_ScalaDomImpl with NorthMR_XML_Info
  }

}

object ScalaXmlSaxBoot extends XmlBoot {
  override def processor(input: String) = input match {
    case zte: String if zte.startsWith("FDD-LTE_MRO_ZTE") => new NorthMR_XML_Reader_ScalaSaxImpl with NorthMR_XML_Info_ZTE
    case _ => new NorthMR_XML_Reader_ScalaSaxImpl with NorthMR_XML_Info
  }
}

object JavaXmlDomBoot extends XmlBoot {
  override def processor(input: String) = input match {
    case zte: String if zte.startsWith("FDD-LTE_MRO_ZTE") => new NorthMR_XML_Reader_JavaDomImpl with NorthMR_XML_Info_ZTE
    case _ => new NorthMR_XML_Reader_JavaDomImpl with NorthMR_XML_Info
  }

}

object JavaXmlSaxBoot extends XmlBoot {
  override def processor(input: String) =  new NorthMR_XML_Reader_JavaSaxImpl {}
}
