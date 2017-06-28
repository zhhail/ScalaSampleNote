package com.zte.bigdata.common.database.gbase


import java.sql._
import com.zte.bigdata.common.{Using, LogSupport}

case class GbaseDataBaseInfo(host: String,
                             port: String,
                             dbName: String,
                             user: String,
                             password: String,
                             driver: String)


class GbaseDatabase(config: GbaseDataBaseInfo) extends LogSupport with Using {
  def query(sqlStatement: String): SqlResult = {
        using(getConnection) {
          conn => DbAccessor.query(conn, sqlStatement)
        }
  }

  def execute(sqlStatement: String): Boolean = {
    using(getConnection) {
      conn =>
        DbAccessor.execute(conn, sqlStatement)
    }
  }

  def execute(sqlStatement: List[String]): Boolean = {
    using(getConnection) {
      conn =>
        sqlStatement.par.map(sql => DbAccessor.execute(conn, sql)).forall(x => x)
    }
  }

  def getConnection: Connection = {
    Class.forName(config.driver)
    DriverManager.getConnection(s"jdbc:gbase://${config.host}:${config.port}/${config.dbName}", config.user, config.password)
  }
}

object DbAccessor extends LogSupport {
  def query(conn: Connection, sql: String): SqlResult = {
    var stmt: Statement = null
    var rs: ResultSet = null
    try {
      stmt = conn.createStatement()
      log.debug(s"Gbase database query: $sql")
      rs = stmt.executeQuery(sql)
      import com.zte.bigdata.common.database.gbase.GbaseDB.ResultSetUtil
      SqlResult(rs.columns, rs.rows)
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

  def execute(conn: Connection, sql: String): Boolean = {
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

