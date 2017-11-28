package com.zte.bigdata.xmlreader.javareader

import com.zte.bigdata.xmlreader.common.NorthMR_XML_Info
import com.zte.bigdata.xmlreader.sax.NorthMR_XML_Reader_JavaSaxImpl
import org.scalatest.FlatSpec


class NorthMR_XML_Reader_JavaSaxImplTest extends FlatSpec  {
  it should "com.zte.bigdata.common sax of java" in {
    val temp = new NorthMR_XML_Reader_JavaSaxImpl with NorthMR_XML_Info
    temp.parseAndSave(Vector("src/test/resources/xml/FDD_LTE_MRO_HUAWEI_639168_20160817080000.xml"), "out/hw.csv")
    temp.parseAndSave(Vector("src/test/resources/xml/FDD-LTE_MRO_NSN_OMC_659001_20160817111500.xml"), "out/nsn.csv")
    temp.parseAndSave(Vector("src/test/resources/xml/FDD-LTE_MRO_ZTE_OMC1_501319_20160713203000.xml"), "out/zte.csv")
  }
  it should "com.zte.bigdata.common sax of java  --  gz source" in {
    val temp = new NorthMR_XML_Reader_JavaSaxImpl with NorthMR_XML_Info
    temp.parseAndSave(Vector("src/test/resources/gz/FDD_LTE_MRO_HUAWEI_639168_20160817013000.xml.gz"), "out/hw_gz.csv")
  }


}
