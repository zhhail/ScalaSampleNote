package com.zte.bigdata.reset_shift

import scala.util.continuations._

class Demo_reset_shift {
  def test1 = {
    var cont: (Unit => Unit) = null
    val fileit = Array("file1", "flie2", "file3").iterator
    var contents = ""
    reset {
      while (contents == "") {
        val fileName = fileit.next()
        try {
          println("try to read " + fileName)
          contents = scala.io.Source.fromFile(fileName, "UTF-8").mkString
        }
        catch {
          case _: Throwable => println("fail to read " + fileName)
        }
        shift {
          k: (Unit => Unit) =>
            cont = k
        }
      }
    }
    while (contents == "" && fileit.hasNext) {
      println("try next file: ")
      cont()
    }
    println(contents)
  }

  def test2 = {
    var cont: (Unit => Unit) = null
    val fileit = Array("file1", "flie2", "file3").iterator
    var contents = ""
    reset {
      println("before shift")
      shift {
        k: (Unit => Unit) =>
          cont = k
          println("in shift")
      }
      println("after shift")
      val fileName = fileit.next()
      try {
        println("try to read " + fileName)
        contents = scala.io.Source.fromFile(fileName, "UTF-8").mkString
      }
      catch {
        case _: Throwable => println("fail to read " + fileName)
      }
    }
    while (contents == "" && fileit.hasNext) {
      println("try next file: ")
      cont()
    }
    println(contents)
  }
  def test3 {
    var cont :String => Int = null
    reset {
      println("before shift")
      val r = shift {
        k:(String=>Int) =>
          println("in shift")
          cont = k
      }
      println("after shift")
      r.toInt+1
    }
    Array(1,2,3,4,5).map(_.toString).foreach{i=>
      val res = cont(i)
      println(s"result: $res")
    }
  }
}
