package com.zte.bigdata.common.database.gbase

import java.sql.ResultSet

object VmaxMonitorConfig {

  val gbaseInfo = GbaseDataBaseInfo(
    host = "10.9.230.176",
    port = "5258",
    dbName = "zxvmax",
    user = "zxvmax",
    password = "ZXvmax2016",
    driver = "com.gbase.jdbc.Driver"
  )
}


object GbaseDB {
  val gbase = new GbaseDatabase(VmaxMonitorConfig.gbaseInfo)

  implicit class ResultSetUtil(rs: ResultSet) {
    private val columnCount = rs.getMetaData.getColumnCount

    def rows: List[List[String]] = {
      var valueList: List[List[String]] = List()
      while (rs.next()) {
        val oneLine = (1 to columnCount).map(rs.getString).toList
        valueList = oneLine :: valueList
      }
      valueList.reverse
    }

    def columns: List[String] = (1 to columnCount).map(rs.getMetaData.getColumnName).toList
  }
}

object GbaseDBAccessor extends GbaseDatabase(VmaxMonitorConfig.gbaseInfo) {

  implicit class ResultSetUtil(rs: ResultSet) {
    private val columnCount = rs.getMetaData.getColumnCount

    def rows: List[List[String]] = {
      var valueList: List[List[String]] = List()
      while (rs.next()) {
        val oneLine = (1 to columnCount).map(rs.getString).toList
        valueList = oneLine :: valueList
      }
      valueList.reverse
    }

    def columns: List[String] = (1 to columnCount).map(rs.getMetaData.getColumnName).toList
  }

}