package com.zte.bigdata.xmlreader.common.javareader

import org.scalatest.FlatSpec
import com.zte.bigdata.xmlreader.common.NorthMR_XML_Info


class NorthMR_XML_Reader_JavaSaxImplTest extends FlatSpec {
  it should "com.zte.bigdata.common sax of java" in {
    val temp = new NorthMR_XML_Reader_JavaSaxImpl with NorthMR_XML_Info
    temp.parseAndSave("src\\test\\resources\\FDD-LTE_MRO_NSN_OMC_659001_20160817111500_sample.xml","out")
  }
}
