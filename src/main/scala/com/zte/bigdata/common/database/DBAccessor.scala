package com.zte.bigdata.common.database

import java.sql._
import com.zte.bigdata.common.database.gbase.SqlResult
import com.zte.bigdata.common.{Using, LogSupport}

trait DBAccessor extends LogSupport with Using {
  val config: DBConfig

  def getConnection: Connection = {
    Class.forName(config.driver)
    DriverManager.getConnection(config.url, config.user, config.password)
  }

  def query(sqlStatement: String): SqlResult = {
    using(getConnection) {
      conn => query(conn, sqlStatement)
    }
  }

  def execute(sqlStatement: String): Boolean = {
    using(getConnection) {
      conn => execute(conn, sqlStatement)
    }
  }

  def execute(sqlStatement: List[String]): Boolean = {
    using(getConnection) {
      conn => sqlStatement.par.map(sql => execute(conn, sql)).forall(x => x)
    }
  }

  private def query(conn: Connection, sql: String): SqlResult = {
    var stmt: Statement = null
    var rs: ResultSet = null
    try {
      stmt = conn.createStatement()
      log.debug(s"Gbase database query: $sql")
      rs = stmt.executeQuery(sql)
      SqlResult(rs)
    } catch {
      case e: Exception => log.error(s"Catch SQLException when executeQuery sql: $sql", e)
        SqlResult(List(), List())
    } finally {
      try {
        if (rs != null) rs.close()
        if (stmt != null) stmt.close()
      } catch {
        case e: SQLException => log.error(s"Catch SQLException in rs.close or stmt.close or conn.close()!", e)
      }
    }
  }

  private def execute(conn: Connection, sql: String): Boolean = {
    var stmt: Statement = null
    try {
      stmt = conn.createStatement()
      log.debug(s"Gbase database execute: $sql")
      stmt.execute(sql)
      true
    } catch {
      case e: SQLException => log.error(s"Catch SQLException when execute sql: $sql", e)
        false
    } finally {
      try {
        if (stmt != null) stmt.close()
      } catch {
        case e: SQLException => log.error(s"Catch SQLException in stmt.close or conn.close()!", e)
      }
    }
  }

}

