package com.zte.bigdata.s99

import org.scalatest.{ShouldMatchers, FlatSpec}

/**
 * Created by 10010581 on 2016/11/28.
 */
class S99ImplTest extends FlatSpec with ShouldMatchers {
  it should "test" in {

  }
  it should "test tree" in {
    pending
    val node = Node('a', Node('b', Node('d'), Node('e')), Node('c', End, Node('f', Node('g'), End)))
    node.allNode(MIDDLE) shouldBe "(d)(b)(e)(a)(c)(g)(f)"
    node.allNode(FORWARD) shouldBe "(a)(b)(d)(e)(c)(f)(g)"
    node.allNode(REVERSE) shouldBe "(d)(e)(b)(g)(f)(c)(a)"
    node.all(MIDDLE) shouldBe "(d)(b)(e)(a)(c)(g)(f)"
    node.all(FORWARD) shouldBe "(a)(b)(d)(e)(c)(f)(g)"
    node.all(REVERSE) shouldBe "(d)(e)(b)(g)(f)(c)(a)"
    node.size() shouldBe 7
    node.isCompleteBalance shouldBe false
    println(node.all(MIDDLE))

  }
  ignore should "P46 fo s-99 (Truth tables for logical expressions.)" in {
    import S99Impl.LogicalOperation._
    and(true, true) shouldBe true
    xor(true, true) shouldBe false
    S99Impl.table2((a: Boolean, b: Boolean) => and(a, or(a, S99Impl.LogicalOperation.not(b)))).foreach(println)
  }
  ignore should "P47 fo s-99 ((*) Truth tables for logical expressions (2).)" in {
    import S99Impl._
    S99Impl.table2((a: Boolean, b: Boolean) => a and (a or S99Impl.LogicalOperation.not(b))).foreach(println)
  }
  ignore should "P49 fo s-99 (Gray code)" in {
    val result = S99Impl.gray(3)
    result should be(List("000", "001", "011", "010", "110", "111", "101", "100"))
    S99Impl.gray1(3) should be(List("000", "001", "011", "010", "110", "111", "101", "100"))
  }
  ignore should "P50 fo s-99 (Huffman code)" in {
    S99Impl.huffman(List(("a", 45), ("b", 13), ("c", 12), ("d", 16), ("e", 9), ("f", 5))) shouldBe
      List(("a", "0"), ("b", "101"), ("c", "100"), ("d", "111"), ("e", "1101"), ("f", "1100"))
    S99Impl.huffman(List(("a", 27), ("b", 8), ("c", 15), ("d", 15), ("e", 30), ("f", 5))) shouldBe
      List(("a", "01"), ("b", "1001"), ("c", "101"), ("d", "00"), ("e", "11"), ("f", "1000"))
  }

  ignore should "P55 (**) Construct completely balanced binary trees." in {
    val cb3 = Tree.cBalanced[String](3, "x")
    cb3.size shouldBe 1
    cb3.head shouldBe Node("x",Node("x"),Node("x"))
    val cb4 = Tree.cBalanced[String](4, "x")
    cb4.size shouldBe 4
    cb4.contains(Node("x",Node("x",Node("x"),End),Node("x"))) shouldBe  true
    cb4.contains(Node("x",Node("x",End,Node("x")),Node("x"))) shouldBe  true
    cb4.contains(Node("x",Node("x"),Node("x",End,Node("x")))) shouldBe  true
    cb4.contains(Node("x",Node("x"),Node("x",Node("x"),End))) shouldBe  true
  }
  ignore should "P56 (**) Symmetric binary trees." in {
    Node('a', Node('b'), Node('c')).isSymmetric shouldBe true
    Node('a').isSymmetric shouldBe true
    Node('a', Node('b'), End).isSymmetric shouldBe false
    Node('a', Node('b',Node('d'),End), Node('c',Node('d'),End)).isSymmetric shouldBe false
  }
  ignore should "P57 (**) Binary search trees (dictionaries)." in  {
    End.addValue(2).addValue(3).addValue(0) shouldBe Node(2,Node(0),Node(3))
    Tree.fromList(List(3, 1, 6, 4, 9)) shouldBe Node(3,Node(1),Node(6,Node(4),Node(9)))
    Tree.fromList(List(5, 3, 18, 1, 4, 12, 21)).isSymmetric shouldBe true
    Tree.fromList(List(3, 2, 5, 7, 4)).isSymmetric shouldBe false
  }
  it should "P58 (**) Generate-and-test paradigm." in {
    Tree.symmetricBalancedTrees(2, 'a) shouldBe List()
    Tree.symmetricBalancedTrees(3, 0) shouldBe List(Node(0,Node(0),Node(0)))
    Tree.symmetricBalancedTrees(4, '#') shouldBe List()
    val st5 = Tree.symmetricBalancedTrees(5, "x")
    st5.size shouldBe 2
    st5.contains(Node("x",Node("x",Node("x"),End),Node("x",End,Node("x")))) shouldBe true
    st5.contains(Node("x",Node("x",End,Node("x")),Node("x",Node("x"),End))) shouldBe true
  }
  it should "P59 (**) Construct height-balanced binary trees." in {

  }
}
