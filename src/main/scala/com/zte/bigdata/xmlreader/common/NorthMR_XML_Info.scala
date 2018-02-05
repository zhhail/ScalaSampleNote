package com.zte.bigdata.xmlreader.common

trait NorthMR_XML_Info {
  val filterColums: Vector[String] = MROConfigPara.filterColums
  val headInfoFromObject: Vector[String] = MROConfigPara.headInfoFromObject
  val eNBIdInXml: String = MROConfigPara.eNBIdInXml

}

trait NorthMR_XML_Info_ZTE extends NorthMR_XML_Info {
  override val headInfoFromObject: Vector[String] = MROConfigPara.headInfoFromObject_zte
  override val eNBIdInXml: String = MROConfigPara.eNBIdInXml_zte
}

