package com.zte.bigdata.xmlreader.common.javareader

import org.scalatest.FlatSpec
import com.zte.bigdata.xmlreader.common.NorthMR_XML_Info

/**
 * Created by 10010581 on 2016/10/19.
 */
class NorthMR_XML_Reader_JavaImplDomTest extends FlatSpec {
  it should "com.zte.bigdata.common Dom of java" in {
    val temp = new NorthMR_XML_Reader_JavaDomImpl with NorthMR_XML_Info
    val r = temp.xml2csv("src\\test\\resources\\FDD-LTE_MRO_NSN_OMC_659001_20160817111500_sample.xml")
    println(r.mkString(",\n") + "\n")
  }
}
