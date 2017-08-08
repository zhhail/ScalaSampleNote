package com.zte.bigdata.common.database.gbase

import java.sql.ResultSet

class SqlResult(val name: List[String], val value: List[List[String]]) {
  def cell(row: Int, column: Int): String = {
    if (row < value.length && column < name.length) value(row)(column)
    else ""
  }

  def cellOption(row: Int, column: Int): Option[String] = {
    if (row < value.length && column < name.length) Some(value(row)(column))
    else None
  }

  def cell(row: Int, columnName: String): String = {
    val column = name.indexOf(columnName)
    if (row < value.length && column < name.length) value(row)(column)
    else ""
  }

  def cellOption(row: Int, columnName: String): Option[String] = {
    val column = name.indexOf(columnName)
    if (row < value.length && column < name.length) Some(value(row)(column))
    else None
  }

  override def toString = {
    (name :: value).map(_.mkString(", ")).mkString("\n")
  }

  def toHiveTableString: String = {
    value.map(_.mkString(",")).mkString(",\n") + ","
  }

  def toCsvFileStringWithTitle: String = {
    name.mkString(",") + ",\n" + toHiveTableString
  }

  def resetColumns(columnNames: List[String]): SqlResult = {
    require(this.name.length == columnNames.length)
    SqlResult(columnNames, this.value)
  }

  def toList: List[List[String]] = name :: value
}

object SqlResult {
  def apply(name: List[String], value: List[List[String]]): SqlResult = new SqlResult(name, value)
  def apply(rs:ResultSet):SqlResult = {
    val columnCount = rs.getMetaData.getColumnCount
    val rows: List[List[String]] = {
      var valueList: List[List[String]] = List()
      while (rs.next()) {
        val oneLine = (1 to columnCount).map(rs.getString).toList
        valueList = oneLine :: valueList
      }
      valueList.reverse
    }
    val columns: List[String] = (1 to columnCount).map(rs.getMetaData.getColumnName).toList
    apply(columns,rows)
  }
}