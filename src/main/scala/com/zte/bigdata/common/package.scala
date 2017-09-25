package com.zte.bigdata

package object common {

  def strSplit(str: String): List[String] = strSplit_best(str)

  protected def strSplit_best(str: String): List[String] = {
    val res = str.foldLeft((List[String](), "")) {
      (r, e) =>
        val (rs, tmpStr) = r
        e match {
          case ',' if tmpStr.count(_ == '"') != 1 => (tmpStr :: rs, "")
          case c => (rs, tmpStr + c)
        }
    }
    (res._2 :: res._1).reverse
  }

  protected def strSplit_better(str: String): List[String] = {
    var rs = List[String]()
    var flag = false
    var e = ""
    str.foreach {
      case ',' if !flag =>
        rs = e :: rs
        e = ""
      case c =>
        if (c == '"') flag = !flag
        e += c
    }
    rs = e :: rs
    rs.reverse
  }


}
