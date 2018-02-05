package com.zte.bigdata.xmlreader.common

import com.zte.bigdata.common.{ConfigSupport, JSONUtil, LogSupport}

import scala.collection.JavaConversions._

sealed trait MROConfigPara extends ConfigSupport {
  override val configFiles = Vector("mro-xml-info.conf")

  val filterColums: Vector[String] = config.getStringList("filter-colums").map(_.toLowerCase).toVector

  val headInfoFromObject: Vector[String] = config.getStringList("nonzte.head-info").toVector
  val eNBIdInXml: String = config.getString("nonzte.enbid")

  val headInfoFromObject_zte: Vector[String] = config.getStringList("zte.head-info").toVector
  val eNBIdInXml_zte: String = config.getString("zte.enbid")
}

object MROConfigPara extends MROConfigPara with LogSupport {
    log.info(
      s"""load configuration:
         |from files: $configFiles
         |""".stripMargin
        + new JSONUtil {}.toJSON(this).split(",").mkString(",\n"))
//  println(new JSONUtil {}.toJSON(this).split(",").mkString(",\n"))
}