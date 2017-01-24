package com.zte.bigdata.s99

import scala.util.Try


object S99Impl extends S99_P46_P47 {
  def main(args: Array[String]) = {
    println(
      """usage: S99Impl width height initx inity strategy
        |strategy can be [min-first|max-first|random]
      """.stripMargin)
    val (width, height) = Try {
      (args(0).toInt, args(1).toInt)
    }.getOrElse((6, 6))
    var (x, y) = Try {
      (args(2).toInt, args(3).toInt)
    }.getOrElse((0, 0))
    var strategy = Try {
      args(4)
    }.getOrElse("min-first")

    val start = System.currentTimeMillis()
    //    showAllTour(width, height)

    oneTour(width, height, (x, y), strategy)
    println(f"cost time: ${System.currentTimeMillis() - start}%12s ms. 策略： $strategy%10s. 棋盘大小 $width%3d * $height%3d, 起始位置： ($x%3d,$y%3d). ")
  }

  def initialize(width: Int, height: Int): ((Int, Int), Set[(Int, Int)]) = {
    val init = (width / 2, height / 2)
    val initPlace: Set[(Int, Int)] = (0 until width).flatMap(x => (0 until height).map(y => (x, y))).toSet
    (init, initPlace)
  }

  def oneTour(width: Int, height: Int, init: (Int, Int), strategy: String = "random"): Unit = {
    println(s"骑士周游问题，棋盘大小 $width * $height, 起始位置： (${init._1},${init._2}})")
    val initPlace: Set[(Int, Int)] = (0 until width).flatMap(x => (0 until height).map(y => (x, y))).toSet
    oneTour(init, initPlace, strategy)
  }

  def oneTour(init: (Int, Int), place: Set[(Int, Int)], strategy: String): Unit = {
    println(s"********************** 路线1(${place.size}) *********************")
    val tour1 = tourOfKnight1(init, place, strategy)
    println(tour1)
  }

  def showAllTour(width: Int, height: Int): Unit = {
    val init = (width / 2, height / 2)
    println(s"骑士周游问题，棋盘大小 $width * $height, 起始位置： (${init._1},${init._2}})")
    val initPlace: Set[(Int, Int)] = (0 until width).flatMap(x => (0 until height).map(y => (x, y))).toSet
    showAllTour(init, initPlace)
  }

  def showAllTour(init: (Int, Int), place: Set[(Int, Int)]): Unit = {
    def isClosed(path: List[(Int, Int)]): Boolean = {
      val (dx, dy) = (Math.abs(path.head._1 - path.last._1), Math.abs(path.head._2 - path.last._2))
      (dx == 1 && dy == 2) || (dx == 2 && dy == 1)
    }
    val totalTour = tourOfKnight(init, place)
    val closedTour = totalTour.filter(isClosed)
    println(s"所有路线数量： ${totalTour.length}， 所有闭合路线数： ${closedTour.length} ")
    println(s"********************** 所有合路线(${place.size}) *********************")
    totalTour.take(3).foreach(println)
    println(s"********************** 所有闭合路线(${place.size}) **********************")
    closedTour.take(3).foreach(println)

  }

  def gray(length: Int): List[String] = length match {
    //  P49 (**) Gray code.
    case 1 => List("0", "1")
    case n => gray(n - 1).map("0" + _) ::: gray(n - 1).reverse.map("1" + _)
  }

  /* 异或转换
  对n位二进制的码字，从右到左，以0到n-1编号
  如果二进制码字的第i位和i+1位相同，则对应的格雷码的第i位为0，否则为1（当i+1=n时，
  二进制码字的第n位被认为是0，即第n-1位不变） */
  def gray1(length: Int): List[String] = {
    //    P49 (**) Gray code.
    def toGray(n: Int): String = n.toBinaryString match {
      case initCode if initCode.length > length => ""
      case initCode =>
        val code = "0" * (length - initCode.length) + initCode
        val gray = code.foldLeft(("", '0')) {
          case (grayCode, char) if char == grayCode._2 => (grayCode._1 + "0", char)
          case (grayCode, char) => (grayCode._1 + "1", char)
          //          (grayCode, char) => (grayCode._1 + {if (char == grayCode._2) "0" else "1"}, char)
        }
        gray._1.foldLeft("")((str, char) => str + char.toString)
    }
    List.range(0, Math.pow(2, length).toInt).map(toGray)
  }

  def huffman(ls: List[(String, Int)]): List[(String, String)] = {
    //    P50(***) Huffman code.
    val nodes: List[HuffmanNode] = ls.map(HuffmanNode(_))
    def huffman(nodes: List[HuffmanNode]): List[HuffmanNode] = {
      (nodes.sortBy(_.value._2) : @unchecked) match {
        case head1 :: head2 :: tail =>
          val newList = HuffmanNode(("root", head1.value._2 + head2.value._2), head1, head2) :: tail
          huffman(newList)
        case head :: Nil => List(head)
      }
    }

    huffman(nodes).head.leafsCode()
  }

  private def check(ls: List[Int]): Boolean = {
    val size = ls.length
    def checkColums(ls: List[Int]): Boolean = ls.distinct.length == size
    def checkCross(ls: List[(Int, Int)]): Boolean = {
      val cross = ls.map(x => x._1 + x._2)
      val rcross = ls.map(x => x._1 - x._2)
      cross.distinct.length == size && rcross.distinct.length == size
    }
    checkColums(ls) && checkCross(ls.zipWithIndex)
  }

  //  效率太低，调用check 16777216 次
  @deprecated
  def eightQueen_slow: List[List[Int]] = {
    var re: List[List[Int]] = Nil
    for (i1 <- 1 to 8) {
      for (i2 <- 1 to 8) {
        for (i3 <- 1 to 8) {
          for (i4 <- 1 to 8) {
            for (i5 <- 1 to 8) {
              for (i6 <- 1 to 8) {
                for (i7 <- 1 to 8) {
                  for (i8 <- 1 to 8) {
                    val r = List(i1, i2, i3, i4, i5, i6, i7, i8)
                    if (check(r)) re = r :: re
                  }
                }
              }
            }
          }
        }
      }
    }
    re
  }

  // 调用check 5500 次。 针对这个算法， check 可以优化成仅检测对角线
  def nQueen1(n: Int): List[List[Int]] = {
    def locate1(done: List[List[Int]], remain: Int): List[List[Int]] = {
      if (remain == 0) done
      else locate1(done.flatMap(x => ((1 to n).toSet -- x.toSet).map(y => x ::: List(y))).filter(check), remain - 1)
    }
    locate1((1 to n).toList.map(List(_)), n - 1)
  }

  // 调用 valid 5509 次。 由于算法的特殊性，valid 可以仅检测对角线
  def nQueen2(n: Int): List[List[Int]] = {
    def validDiagonals(qs: List[Int], upper: Int, lower: Int): Boolean = qs match {
      case Nil => true
      case q :: tail => q != upper && q != lower && validDiagonals(tail, upper + 1, lower - 1)
    }
    def valid(qs: List[Int]): Boolean = qs match {
      case Nil => true
      case q :: tail => validDiagonals(tail, q + 1, q - 1)
    }
    def getqueues(queens: List[Int], remainColums: Set[Int]): List[List[Int]] = {
      if (!valid(queens)) Nil
      else if (remainColums.isEmpty) List(queens)
      else remainColums.toList.flatMap(x => getqueues(x :: queens, remainColums - x))
    }
    getqueues(Nil, (1 to n).toSet)
  }

  def eightQueens: List[List[Int]] = nQueen1(8)

  def tourOfKnight1(init: (Int, Int), initPlace: Set[(Int, Int)], strategy: String = "min-first"): List[(Int, Int)] = {
    case class TourFound(result: List[(Int, Int)]) extends Exception("")
    def getNext(xy: (Int, Int)): List[(Int, Int)] = grid.map(e => (xy._1 + e._1, xy._2 + e._2))
    def tour(path: List[(Int, Int)], place: Set[(Int, Int)]): List[(Int, Int)] = {
      if (path == Nil) Nil
      else if (place.size == 0) {
        throw new TourFound(path)
      }
      else {
        val nexts = getNext(path.last).filter(place.contains)
        if (nexts == Nil) Nil
        else {
          //          val sortedNexts1 = nexts.map(x=>(x,getNext(x).filter(place.contains).length)).sortBy(_._2).map(_._1)
          val sortedNexts = if (strategy == "min-first") nexts.map(x => (x, getNext(x).count(place.contains))).sortBy(_._2).map(_._1)
          else if (strategy == "max-first") nexts.map(x => (x, getNext(x).count(place.contains))).sortBy(-_._2).map(_._1)
          else nexts
          sortedNexts.flatMap(next => tour((next :: path.reverse).reverse, place - next))
        }
      }
    }
    try {
      tour(List(init), initPlace - init)
    }
    catch {
      case e: TourFound => e.result
    }
  }

  private val grid = List(-2, -1, 1, 2).flatMap(x => List(-2, -1, 1, 2).map(y => (x, y))).filter(x => x._1 * x._1 != x._2 * x._2)

  def tourOfKnight(init: (Int, Int), initPlace: Set[(Int, Int)]): List[List[(Int, Int)]] = {
    def getNext(xy: (Int, Int)): List[(Int, Int)] = grid.map(e => (xy._1 + e._1, xy._2 + e._2))

    var times = 0
    def tour(path: List[(Int, Int)], place: Set[(Int, Int)]): List[List[(Int, Int)]] = {
      times += 1
      if (times % 10000000 == 0) {
        times = 0
        println(s"call tour 1 kw times......")
      }
      if (path == Nil) Nil
      else if (place.size == 0) List(path)
      else {
        val nexts = getNext(path.last).filter(place.contains)
        if (nexts == Nil) Nil
        else nexts.par.flatMap(next => tour((next :: path.reverse).reverse, place - next)).toList
      }
    }
    //    val inittimes = times
    val result = tour(List(init), initPlace - init)
    //    println(s"call tour ${times - inittimes} times")
    result
  }

  private val num2str = Map('1' -> "one",
    '2' -> "two",
    '3' -> "three",
    '4' -> "four",
    '5' -> "five",
    '6' -> "six",
    '7' -> "seven",
    '8' -> "eight",
    '9' -> "nine",
    '0' -> "zero")

  def fullWords(num: Int): String = num.toString.map(num2str(_)).mkString("-")

}

case class HuffmanNode(value1: (String, Int), left1: Tree[(String, Int)], right1: Tree[(String, Int)]) extends Node[(String, Int)](value1, left1, right1) {
  def leafsCode(): List[(String, String)] = {
    def getleafsCode(nodeAndCode: (HuffmanNode, String)): List[(String, String)] = nodeAndCode match {
      case (node: HuffmanNode, code: String) => node match {
        case HuffmanNode(_, End, End) => List((node.value._1, code))
        case HuffmanNode(_, left1: HuffmanNode, End) => getleafsCode(left1, code + "0")
        case HuffmanNode(_, End, right1: HuffmanNode) => getleafsCode(right1, code + "1")
        case HuffmanNode(_, left1: HuffmanNode, right1: HuffmanNode) => getleafsCode(left1, code + "0") ::: getleafsCode(right1, code + "1")
      }
    }
    getleafsCode((this, "")).sortBy(_._1)
  }
}

object HuffmanNode {
  def apply(value: Tuple2[String, Int]): HuffmanNode = HuffmanNode(value, End, End)
}

case class PositionedValue[+T](value: T, x: Int, y: Int)

case class PositionedNode[+T](value1: PositionedValue[T], left1: Tree[PositionedValue[T]], right1: Tree[PositionedValue[T]]) extends Node[PositionedValue[T]](value1, left1, right1) {
  override def toString = "T[" + value.x.toString + "," + value.y.toString + "](" + value.value.toString + " " + left.toString + " " + right.toString + ")"
}

object Sudoku {

  case class SudokuElement(done: Boolean, digital: Int, maybe: List[Int])

  private def ls2vvi(ls: Array[String]): Vector[Vector[Int]] = ls.toVector.map(_.toVector.map(_.toString.toInt))

  private def str2vvi(str: String): Vector[Vector[Int]] = ls2vvi(str.split("\n"))

  def sudoku(str: String): Vector[Vector[Vector[Int]]] = sudoku(str2vvi(str))
  def sudoku(str: Array[String]): Vector[Vector[Vector[Int]]] = sudoku(ls2vvi(str))

  def sudoku(su: Vector[Vector[Int]]): Vector[Vector[Vector[Int]]] = {
    def fix1Digtal(input: Vector[Vector[SudokuElement]]): Vector[Vector[Vector[SudokuElement]]] = {
      def getExistDigtal(i: Int, j: Int): (List[Int], List[Int], List[Int]) = {
        var existDigl = List[Int]()
        var existDigc = List[Int]()
        var existDig3 = List[Int]()
        for (k <- 0 to 8) {
          existDigl = input(k)(j).digital :: existDigl
          existDigc = input(i)(k).digital :: existDigc
        }
        for (l <- i / 3 * 3 to i / 3 * 3 + 2) {
          for (m <- j / 3 * 3 to j / 3 * 3 + 2) {
            existDig3 = input(l)(m).digital :: existDig3
          }
        }
        (existDigl, existDigc, existDig3)
      }
      def maybyList(i: Int, j: Int): List[Int] = {
        val existDig = {
          val tmp = getExistDigtal(i, j);
          tmp._1 ::: tmp._2 ::: tmp._3
        }
        ((1 to 9).toSet -- existDig.toSet).toList
      }

      var fixed1 = false
      var (mbsize, x, y) = (10, 10, 10)
      val maybe_all = for (i <- (0 to 8).toVector) yield {
        for (j <- (0 to 8).toVector) yield {
          if (input(i)(j).done || fixed1) input(i)(j)
          else {
            val mbl = maybyList(i, j)
            if (mbl.length < mbsize) {
              mbsize = mbl.length
              x = i
              y = j
            }
            if (mbl.length == 1) {
              fixed1 = true
              SudokuElement(true, mbl.head, Nil)
            }
            else SudokuElement(false, 0, mbl)
          }
        }
      }
      if (input.map(_.count(!_.done)).sum == 0) Vector(input)
      else if (fixed1) fix1Digtal(maybe_all)
      else {
        val validate = maybe_all.flatten.forall(e => e.done || e.maybe.length != 0)
        if (!validate) Vector()
        else {
          // 找到 maybe 包含元素最少的，展开成Vector
          val maybe_alls = for (k <- (0 until mbsize).toVector) yield {
            for (i <- (0 to 8).toVector) yield {
              for (j <- (0 to 8).toVector) yield {
                if (i == x && j == y) SudokuElement(true, maybe_all(i)(j).maybe(k), Nil)
                else maybe_all(i)(j)
              }
            }
          }
          maybe_alls.flatMap(fix1Digtal)
        }
      }
    }
    val sudo = su.map(y => y.map(x => if (x > 0 && x < 10) SudokuElement(true, x, Nil) else SudokuElement(false, 0, Nil)))
    fix1Digtal(sudo).map(_.map(_.map(_.digital)))
  }

  def showResult(re: Vector[Vector[Vector[Int]]]): Unit = {
    println("")
    println("************** 解的个数: " + re.length)
    re.foreach {
      line =>
        println("----------------------------------")
        val sep = " "
        line.foreach(x => println(sep + x.mkString(sep) + sep))
    }
  }
}

