package com.zte.bigdata

package object common {

  def strSplit(str: String): Iterable[String] = strSplit_best(str)

  /**
    * @param str              待分割的字符
    * @param delimiter        分隔符
    * @param string_qualifier 定界符。被定界符包括起来的数据，会被当成一个整体，不按照 delimiter 分割
    * @return 分割后的字符串数组
    */
  def strSplit_best(str: String, delimiter: String = ",", string_qualifier: String = "\""): Iterable[String] = {
    if (!str.contains(string_qualifier)) {
      str.split(delimiter, -1)
    }
    else {
      val subStr = str.split(string_qualifier).zipWithIndex
      if (str.startsWith(string_qualifier)) subStr.drop(1).flatMap {
        case (v, i) if i % 2 == 1 => Array(string_qualifier + v + string_qualifier)
        case (v, i) if i == subStr.length - 1 => v.drop(1).split(delimiter, -1)
        case (v, _) => v.dropRight(1).drop(1).split(delimiter, -1)
      }
      else subStr.flatMap {
        case (v, 0) => v.dropRight(1).split(delimiter, -1)
        case (v, i) if i % 2 == 1 => Array(string_qualifier + v + string_qualifier)
        case (v, i) if i == subStr.length - 1 => v.drop(1).split(delimiter, -1)
        case (v, _) => v.dropRight(1).drop(1).split(delimiter, -1)
      }
    }
  }


}
