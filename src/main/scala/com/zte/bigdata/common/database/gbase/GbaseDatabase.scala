package com.zte.bigdata.common.database.gbase


import java.sql._
import com.zte.bigdata.common.{Using, LogSupport}

case class GbaseDataBaseInfo(host: String,
                             port: String,
                             dbName: String,
                             user: String,
                             password: String,
                             driver: String)

object GbaseDatabase {
  val gbaseInfo = GbaseDataBaseInfo(
    host = "10.9.230.176",
    port = "5258",
    dbName = "zxvmax",
    user = "zxvmax",
    password = "ZXvmax2016",
    driver = "com.gbase.jdbc.Driver")

  val gbase = new GbaseDatabase() {
    override val config: GbaseDataBaseInfo = gbaseInfo
  }

  trait GbaseDatabase extends DBAccessor {
    override def getConnection: Connection = {
      val url = s"jdbc:gbase://${config.host}:${config.port}/${config.dbName}"
      Class.forName(config.driver)
      DriverManager.getConnection(url, config.user, config.password)
    }
  }

  trait DBAccessor extends LogSupport with Using {
    def getConnection: Connection

    val config: GbaseDataBaseInfo

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
        val rsUtil = new ResultSetUtil(rs)
        SqlResult(rsUtil.columns, rsUtil.rows)
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

}


