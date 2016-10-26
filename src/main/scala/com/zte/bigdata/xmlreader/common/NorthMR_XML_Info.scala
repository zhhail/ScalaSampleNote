package com.zte.bigdata.xmlreader.common

trait NorthMR_XML_Info {
  val filterColums = Vector("MR.LteScEarfcn",
    /*"MR.LteScAOA",
    "MR.LteScPHR",
    "MR.LteScPci",
    "MR.LteScRSRP",
    "MR.LteScRSRQ",
    "MR.LteScSinrUL",
    "MR.LteScTadv",*/
    "MR.LteNcEarfcn",
    "MR.LteNcPci",
    "MR.LteNcRSRP",
    "MR.LteNcRSRQ",
        "MR.Latitude" /*,
        "MR.LatitudeSign",
        "MR.Longitude"*/)
  val headInfoFromObject = Vector(
    "TimeStamp",
    "MmeUeS1apId",
    "MmeGroupId",
    "MmeCode")
  val eNBIdInXml = "id"
}

trait NorthMR_XML_Reader {
  def parseAndSave(xmlFileName: String, outFileName: String): Unit
}

trait NorthMR_XML_Info_ZTE extends NorthMR_XML_Info {
  override val eNBIdInXml = "MR.eNBId"
  override val headInfoFromObject = Vector(
    "MR.TimeStamp",
    "MR.MmeUeS1apId",
    "MR.MmeGroupId",
    "MR.MmeCode")
}
