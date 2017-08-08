package com.zte.bigdata.common.database.gbase

import java.sql.ResultSet

class ResultSetUtil(rs: ResultSet) {
  private val columnCount = rs.getMetaData.getColumnCount

  val rows: List[List[String]] = {
    var valueList: List[List[String]] = List()
    while (rs.next()) {
      val oneLine = (1 to columnCount).map(rs.getString).toList
      valueList = oneLine :: valueList
    }
    valueList.reverse
  }

  val columns: List[String] = (1 to columnCount).map(rs.getMetaData.getColumnName).toList
}
