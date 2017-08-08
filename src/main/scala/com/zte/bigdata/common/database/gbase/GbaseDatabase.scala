package com.zte.bigdata.common.database.gbase


import com.zte.bigdata.common.database.{DBAccessor, DBConfig}


object GbaseDatabase {
  lazy val gbase = new DBAccessor() {
    override val config: DBConfig = gbaseInfo
  }

  val gbaseInfo = {
    val host = "10.9.230.176"
    val port = "5258"
    val dbName = "zxvmax"
    DBConfig(
      url = s"jdbc:gbase://$host:$port/$dbName",
      user = "zxvmax",
      password = "ZXvmax2016",
      driver = "com.gbase.jdbc.Driver"
    )
  }

}


