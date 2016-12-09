package com.zte.bigdata.s99

/**
 * Created by 10010581 on 2016/11/28.
 */
object S99Impl extends S99_P46_P47 {
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
      case initCode => {
        val code = "0" * (length - initCode.length) + initCode
        val gray = code.foldLeft(("", '0')) {
          case (grayCode, char) if char == grayCode._2 => (grayCode._1 + "0", char)
          case (grayCode, char) => (grayCode._1 + "1", char)
          //          (grayCode, char) => (grayCode._1 + {if (char == grayCode._2) "0" else "1"}, char)
        }
        gray._1.foldLeft("")((str, char) => str + char.toString)
      }
    }
    List.range(0, Math.pow(2, length).toInt).map(toGray)
  }

  def huffman(ls: List[(String, Int)]): List[(String, String)] = {
    //    P50(***) Huffman code.
    val nodes: List[HuffmanNode] = ls.map(HuffmanNode(_))
    def huffman(nodes: List[HuffmanNode]): List[HuffmanNode] = {
      nodes.sortBy(_.value._2) match {
        case head1 :: head2 :: tail =>
          val newList = HuffmanNode(("root", head1.value._2 + head2.value._2), head1, head2) :: tail
          huffman(newList)
        case head :: Nil => List(head)
      }
    }

    huffman(nodes).head.leafsCode()
  }

  def fullWords(num: Int):String = {
    val num2str = Map('1' -> "one", '2' -> "two", '3' -> "three", '4' -> "four", '5' -> "five", '6' -> "six", '7' -> "seven", '8' -> "eight", '9' -> "nine", '0' -> "zero")
    num.toString.map(num2str(_)).mkString("-")
  }

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