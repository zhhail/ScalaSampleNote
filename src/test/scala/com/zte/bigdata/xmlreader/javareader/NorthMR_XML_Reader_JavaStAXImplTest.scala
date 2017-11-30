package com.zte.bigdata.xmlreader.javareader

import java.io.File

import com.zte.bigdata.xmlreader.common.NorthMR_XML_Info
import com.zte.bigdata.xmlreader.stax.NorthMR_XML_Reader_JavaStAXImpl
import org.scalatest.FlatSpec

class NorthMR_XML_Reader_JavaStAXImplTest extends FlatSpec {
  val temp = new NorthMR_XML_Reader_JavaStAXImpl with NorthMR_XML_Info
  it should "StAX of java -- gz source" in {
    temp.parseAndSave(Vector("src/test/resources/gz/FDD-LTE_MRO_HUAWEI_86493_20170902043000.xml.gz"), "out/stax_hw_gz.csv")
  }
  it should "stax of java  --  tgzgz source" in {
    pending
    temp.parseAndSave(Vector("src/test/resources/tgz/HW_HN_OMC1-mr-134.175.57.16-20170921043000-20170921044500-20170921051502-001.tar.gz"), "out/hw_tgzgz.csv")
  }
  it should "StAX of java -- all gz source" in {
    val files = new File("src/test/resources/gz/").listFiles().map(_.getName).filter(_.contains("MRO"))
    temp.parseAndSave(files.map(x => "src/test/resources/gz/" + x).toVector, "out/stax_hw_gz_all.csv")
  }

}
