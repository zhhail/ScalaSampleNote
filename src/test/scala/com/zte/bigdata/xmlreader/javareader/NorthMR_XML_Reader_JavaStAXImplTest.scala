package com.zte.bigdata.xmlreader.javareader

import com.zte.bigdata.xmlreader.common.NorthMR_XML_Info
import com.zte.bigdata.xmlreader.stax.NorthMR_XML_Reader_JavaStAXImpl
import org.scalatest.FlatSpec

class NorthMR_XML_Reader_JavaStAXImplTest extends FlatSpec {
  it should "com.zte.bigdata.common StAX of java" in {
    val temp = new NorthMR_XML_Reader_JavaStAXImpl with NorthMR_XML_Info
    temp.parseAndSave(Vector("src/test/resources/gz/FDD_LTE_MRO_HUAWEI_639168_20160817013000.xml.gz"), "out/stax_hw.csv")
  }

}
