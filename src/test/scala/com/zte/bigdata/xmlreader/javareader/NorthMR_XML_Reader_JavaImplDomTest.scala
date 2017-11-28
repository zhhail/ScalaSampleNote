package com.zte.bigdata.xmlreader.javareader

import org.scalatest.FlatSpec
import com.zte.bigdata.xmlreader.common.NorthMR_XML_Info
import com.zte.bigdata.xmlreader.dom.NorthMR_XML_Reader_JavaDomImpl

/**
 * Created by 10010581 on 2016/10/19.
 */
class NorthMR_XML_Reader_JavaImplDomTest extends FlatSpec {
  it should "com.zte.bigdata.common Dom of java" in {
    val temp = new NorthMR_XML_Reader_JavaDomImpl with NorthMR_XML_Info
    val r = temp.xml2csv("src/test/resources/gz/FDD_LTE_MRO_HUAWEI_639168_20160817014500.xml.gz")
//    println(r.mkString(",\n") + "\n")
    println("parse file complete, output length: "+r.length)
  }
}
