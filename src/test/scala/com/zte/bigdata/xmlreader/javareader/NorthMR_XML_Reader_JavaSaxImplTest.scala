package com.zte.bigdata.xmlreader.javareader

import com.zte.bigdata.xmlreader.common.NorthMR_XML_Info
import com.zte.bigdata.xmlreader.sax.NorthMR_XML_Reader_JavaSaxImpl
import org.scalatest.FlatSpec

import java.io.File


class NorthMR_XML_Reader_JavaSaxImplTest extends FlatSpec  {
  val temp = new NorthMR_XML_Reader_JavaSaxImpl with NorthMR_XML_Info
  ignore should "sax of java xml source" in {
    // 不再支持xml文件，仅支持gz后的xml
    temp.parseAndSave(Vector("src/test/resources/xml/FDD-LTE_MRO_HUAWEI_86493_20170902043000.xml"), "out/hw.csv")
    temp.parseAndSave(Vector("src/test/resources/xml/FDD-LTE_MRO_NSN_OMC_659001_20160817111500.xml"), "out/nsn.csv")
    temp.parseAndSave(Vector("src/test/resources/xml/FDD-LTE_MRO_ZTE_OMC1_501319_20160713203000.xml"), "out/zte.csv")
  }
  it should "sax of java  --  gz source" in {
    temp.parseAndSave(Vector("src/test/resources/gz/FDD-LTE_MRO_HUAWEI_86493_20170902043000.xml.gz"), "out/hw_gz.csv")
  }
  it should "sax of java  --  tgzgz source" in {
    pending
    temp.parseAndSave(Vector("src/test/resources/tgz/HW_HN_OMC1-mr-134.175.57.16-20170921043000-20170921044500-20170921051502-001.tar.gz"), "out/hw_tgzgz.csv")
  }
  it should "sax of java  --  all gz source" in {
    val files = new File("src/test/resources/gz/").listFiles().map(_.getName).filter(_.contains("MRO"))
    temp.parseAndSave(files.map(x=>"src/test/resources/gz/"+x).toVector,"out/hw_gz_all.csv")

  }

}
