package com.zte.bigdata.s99

import org.scalatest.{ShouldMatchers, FlatSpec}
import S99Impl._

class S99ImplTest extends FlatSpec with ShouldMatchers {
  it should "test" in {
  }
  it should "test tree" in {
    val node = Node('a', Node('b', Node('d'), Node('e')), Node('c', End, Node('f', Node('g'), End)))
    node.size shouldBe 7
    node.isCompleteBalance shouldBe false
    node.height shouldBe (4)
  }
  it should "P46 (**) Truth tables for logical expressions." in {
    import LogicalOperation._
    and(true, true) shouldBe true
    xor(true, true) shouldBe false
    table2((a: Boolean, b: Boolean) => and(a, or(a, S99Impl.LogicalOperation.not(b)))).foreach(println)
  }
  it should "P47 (*) Truth tables for logical expressions (2)." in {
    table2((a: Boolean, b: Boolean) => a and (a or S99Impl.LogicalOperation.not(b))).foreach(println)
  }
  it should "P49 (**) Gray code." in {
    val result = S99Impl.gray(3)
    result should be(List("000", "001", "011", "010", "110", "111", "101", "100"))
    gray1(3) should be(List("000", "001", "011", "010", "110", "111", "101", "100"))
  }
  it should "P50 (***) Huffman code." in {
    huffman(List(("a", 45), ("b", 13), ("c", 12), ("d", 16), ("e", 9), ("f", 5))) shouldBe
      List(("a", "0"), ("b", "101"), ("c", "100"), ("d", "111"), ("e", "1101"), ("f", "1100"))
    huffman(List(("a", 27), ("b", 8), ("c", 15), ("d", 15), ("e", 30), ("f", 5))) shouldBe
      List(("a", "01"), ("b", "1001"), ("c", "101"), ("d", "00"), ("e", "11"), ("f", "1000"))
  }

  it should "P55 (**) Construct completely balanced binary trees." in {
    val cb3 = Tree.cBalanced[String](3, "x")
    cb3.size shouldBe 1
    cb3.head shouldBe Node("x", Node("x"), Node("x"))
    val cb4 = Tree.cBalanced[String](4, "x")
    cb4.size shouldBe 4
    cb4.contains(Node("x", Node("x", Node("x"), End), Node("x"))) shouldBe true
    cb4.contains(Node("x", Node("x", End, Node("x")), Node("x"))) shouldBe true
    cb4.contains(Node("x", Node("x"), Node("x", End, Node("x")))) shouldBe true
    cb4.contains(Node("x", Node("x"), Node("x", Node("x"), End))) shouldBe true
  }
  it should "P56 (**) Symmetric binary trees." in {
    Node('a', Node('b'), Node('c')).isSymmetric shouldBe true
    Node('a').isSymmetric shouldBe true
    Node('a', Node('b'), End).isSymmetric shouldBe false
    Node('a', Node('b', Node('d'), End), Node('c', Node('d'), End)).isSymmetric shouldBe false
  }
  it should "P57 (**) Binary search trees (dictionaries)." in {
    End.addValue(2).addValue(3).addValue(0) shouldBe Node(2, Node(0), Node(3))
    Tree.fromList(List(3, 1, 6, 4, 9)) shouldBe Node(3, Node(1), Node(6, Node(4), Node(9)))
    Tree.fromList(List(5, 3, 18, 1, 4, 12, 21)).isSymmetric shouldBe true
    Tree.fromList(List(3, 2, 5, 7, 4)).isSymmetric shouldBe false
  }
  it should "P58 (**) Generate-and-test paradigm." in {
    Tree.symmetricBalancedTrees(2, 'a) shouldBe List()
    Tree.symmetricBalancedTrees(3, 0) shouldBe List(Node(0, Node(0), Node(0)))
    Tree.symmetricBalancedTrees(4, '#') shouldBe List()
    val st5 = Tree.symmetricBalancedTrees(5, "x")
    st5.size shouldBe 2
    st5.contains(Node("x", Node("x", Node("x"), End), Node("x", End, Node("x")))) shouldBe true
    st5.contains(Node("x", Node("x", End, Node("x")), Node("x", Node("x"), End))) shouldBe true
  }
  it should "P59 (**) Construct height-balanced binary trees." in {
    End.height shouldBe 0
    Node("x").height shouldBe 1
    Node(1, Node(1), End).height shouldBe 2
    Node(1, Node(1, Node(1), End), End).height shouldBe 3

    Node("x").isHeightBalance shouldBe true
    Node(1, Node(1), End).isHeightBalance shouldBe true
    Node(1, End, Node(1)).isHeightBalance shouldBe true
    Node(1, Node(1, Node(1), End), End).isHeightBalance shouldBe false

    val hbalt2 = Tree.hbalTrees(2, "x")
    hbalt2.length shouldBe 3
    hbalt2.contains(Node("x", Node("x"), End)) shouldBe true
    hbalt2.contains(Node("x", End, Node("x"))) shouldBe true
    hbalt2.contains(Node("x", Node("x"), Node("x"))) shouldBe true
    val hbalt3 = Tree.hbalTrees(3, 0)
    hbalt3.length shouldBe 15
    hbalt3.contains(Node(0, Node(0), Node(0, Node(0), End))) shouldBe true
    hbalt3.contains(Node(0, Node(0), Node(0, Node(0), Node(0)))) shouldBe true
    hbalt3.contains(Node(0, Node(0, Node(0), End), Node(0, Node(0), Node(0)))) shouldBe true
    hbalt3.contains(Node(0, Node(0, Node(0), Node(0)), Node(0, Node(0), Node(0)))) shouldBe true
    Tree.hbalTrees(4, 0).length shouldBe 315
    Tree.hbalTrees(5, 0).length shouldBe 108675
  }
  it should "P60 (**) Construct height-balanced binary trees with a given number of nodes." in {
    Tree.minHbalNodes(3) shouldBe 4
    Tree.maxHbalHeight(4) shouldBe 3
    val hbts = Tree.hbalTreesWithNodes(4, "x")
    hbts.length shouldBe 4
    hbts.contains(Node("x", Node("x", Node("x"), End), Node("x"))) shouldBe true
    hbts should contain(Node("x", Node("x", End, Node("x")), Node("x")))
    hbts should contain(Node("x", Node("x"), Node("x", Node("x"), End)))
    hbts should contain(Node("x", Node("x"), Node("x", End, Node("x"))))
    Tree.hbalTreesWithNodes(15, "x").length shouldBe 1553
  }
  it should "P61 (*) Count the leaves of a binary tree." in {
    Node('x', Node('x'), End).leafCount() shouldBe 1
    Node('x', Node('x'), Node("x")).leafCount() shouldBe 2
    Node('x', Node('x', Node("x"), End), Node("x")).leafCount() shouldBe 2
    Node('x', Node('x', Node("x"), Node("x")), Node("x")).leafCount() shouldBe 3
  }
  it should "61A (*) Collect the leaves of a binary tree in a list." in {
    Node('a', Node('b'), Node('c', Node('d'), Node('e'))).leafList shouldBe List('b', 'd', 'e')
  }
  it should "P62 (*) Collect the internal nodes of a binary tree in a list." in {
    Node('a', Node('b'), Node('c', Node('d'), Node('e'))).internalList shouldBe List('a', 'c')
  }
  it should "P62B (*) Collect the nodes at a given level in a list." in {
    Node('a', Node('b'), Node('c', Node('d'), Node('e'))).atLevel(2) shouldBe List('b', 'c')
    Node('a', Node('b'), Node('c', Node('d'), Node('e'))).atLevel(3) shouldBe List('d', 'e')
  }
  it should "P63 (**) Construct a complete binary tree." in {
    Tree.completeBinaryTree(4, "x") shouldBe Node("x", Node("x", Node("x"), End), Node("x"))
    Tree.completeBinaryTree(6, "x") shouldBe Node("x", Node("x", Node("x"), Node("x")), Node("x", Node("x"), End))
  }
  it should "P64 (**) Layout a binary tree (1)." in {
    Node('a', Node('b', End, Node('c')), Node('d')).layoutBinaryTree.toString shouldBe "T[3,1](a T[1,2](b . T[2,3](c . .)) T[4,2](d . .))"
    Node('a', Node('b', End, Node('c')), Node('d')).layoutBinaryTree shouldBe PositionedNode(PositionedValue('a', 3, 1), PositionedNode(PositionedValue('b', 1, 2), End, PositionedNode(PositionedValue('c', 2, 3), End, End)), PositionedNode(PositionedValue('d', 4, 2), End, End))
    Node('a').layoutBinaryTree shouldBe PositionedNode(PositionedValue('a', 1, 1), End, End)
    Tree.fromList(List('n', 'k', 'm', 'c', 'a', 'h', 'g', 'e', 'u', 'p', 's', 'q')).layoutBinaryTree.toString shouldBe "T[8,1](n T[6,2](k T[2,3](c T[1,4](a . .) T[5,4](h T[4,5](g T[3,6](e . .) .) .)) T[7,3](m . .)) T[12,2](u T[9,3](p . T[11,4](s T[10,5](q . .) .)) .))"
  }
  it should "P65 (**) Layout a binary tree (2)." in {
    Node('a').layoutBinaryTree2 shouldBe PositionedNode(PositionedValue('a', 1, 1), End, End)
    Node('a', Node('b', Node('c'), End), Node('d')).layoutBinaryTree2 shouldBe PositionedNode(PositionedValue('a', 4, 1), PositionedNode(PositionedValue('b', 2, 2), PositionedNode(PositionedValue('c', 1, 3), End, End), End), PositionedNode(PositionedValue('d', 6, 2), End, End))
    Node('a', Node('b', End, Node('c')), Node('d')).layoutBinaryTree2 shouldBe PositionedNode(PositionedValue('a', 3, 1), PositionedNode(PositionedValue('b', 1, 2), End, PositionedNode(PositionedValue('c', 2, 3), End, End)), PositionedNode(PositionedValue('d', 5, 2), End, End))
    Tree.fromList(List('n', 'k', 'm', 'c', 'a', 'e', 'd', 'g', 'u', 'p', 'q')).layoutBinaryTree2.toString shouldBe "T[15,1](n T[7,2](k T[3,3](c T[1,4](a . .) T[5,4](e T[4,5](d . .) T[6,5](g . .))) T[11,3](m . .)) T[23,2](u T[19,3](p . T[21,4](q . .)) .))"
  }
  it should "P66 (***) Layout a binary tree (3)." in {
    Node('a', Node('b', End, Node('c')), Node('d')).layoutBinaryTree3.toString shouldBe "T[2,1](a T[1,2](b . T[2,3](c . .)) T[3,2](d . .))"
    Tree.fromList(List('n', 'k', 'm', 'c', 'a', 'e', 'd', 'u', 'p', 'q')).layoutBinaryTree3.toString shouldBe "T[5,1](n T[3,2](k T[2,3](c T[1,4](a . .) T[3,4](e T[2,5](d . .) .)) T[4,3](m . .)) T[7,2](u T[6,3](p . T[7,4](q . .)) .))"
    Tree.fromList(List('n', 'k', 'm', 'c', 'a', 'e', 'd', 'g', 'u', 'p', 'q')).layoutBinaryTree3.toString shouldBe "T[5,1](n T[3,2](k T[2,3](c T[1,4](a . .) T[3,4](e T[2,5](d . .) T[4,5](g . .))) T[4,3](m . .)) T[7,2](u T[6,3](p . T[7,4](q . .)) .))"
    Node('a',Node('b',Node('c'),End),Node('d',Node('e',Node('f',Node('g',Node('h'),End),End),End),End)).layoutBinaryTree3.toString shouldBe "T[4,1](a T[3,2](b T[2,3](c . .) .) T[5,2](d T[4,3](e T[3,4](f T[2,5](g T[1,6](h . .) .) .) .) .))"
  }
  it should "P67 (**) A string representation of binary trees." in {
    Node('a', Node('b', Node('d'), Node('e')), Node('c', End, Node('f', Node('g'), End))).toString shouldBe "a(b(d,e),c(,f(g,)))"
    Tree.fromString("a(b(d,e),c(,f(g,)))") shouldBe Node('a', Node('b', Node('d'), Node('e')), Node('c', End, Node('f', Node('g'), End)))
  }
  it should "P68 (**) Preorder and inorder sequences of binary trees." in {
    Tree.string2Tree("a(b(d,e),c(,f(g,)))").preorder shouldBe List('a', 'b', 'd', 'e', 'c', 'f', 'g')
    Tree.string2Tree("a(b(d,e),c(,f(g,)))").inorder shouldBe List('d', 'b', 'e', 'a', 'c', 'g', 'f')
    Tree.preInTree(List('a', 'b', 'd', 'e', 'c', 'f', 'g'), List('d', 'b', 'e', 'a', 'c', 'g', 'f')) shouldBe Node('a', Node('b', Node('d'), Node('e')), Node('c', End, Node('f', Node('g'), End)))
  }
  it should "P69 (**) Dotstring representation of binary trees." in {
    Tree.string2Tree("a(b(d,e),c(,f(g,)))").toDotstring shouldBe "abd..e..c.fg..."
    Tree.fromDotstring("abd..e..c.fg...") shouldBe Node('a', Node('b', Node('d'), Node('e')), Node('c', End, Node('f', Node('g'), End)))
  }
  it should "P95 (**) English number words." in {
    fullWords(1764) shouldBe "one-seven-six-four"
    fullWords(109510) shouldBe "one-zero-nine-five-one-zero"
  }
}


