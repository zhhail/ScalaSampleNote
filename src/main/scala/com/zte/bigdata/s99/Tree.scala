package com.zte.bigdata.s99

trait Tree[+T] {
  def size(): Int

  def height(): Int

  def isSymmetric: Boolean = true

  def addValue[U >: T <% Ordered[U]](x: U): Tree[U]

  def isCompleteBalance: Boolean

  def isFull: Boolean

  def isHeightBalance: Boolean

  def atLevel(level: Int): List[T]


}


object Tree {
  // 构造给定节点数的所有 完全平衡二叉树 completely balanced binary tree.
  // 定义： 对任意节点，左右树的节点数最多相差1个
  def cBalanced[T](numOfNodes: Int, value: T): List[Node[T]] =
    makeTree_byNodes(numOfNodes, value).filter(_.isCompleteBalance).toList

  //  从一个list构造binary search tree.
  def fromList[U <% Ordered[U]](ls: List[U]): Node[U] =
    ls.tail.foldLeft(Node(ls.head))((r, e) => r.addValue(e))

  // 构造给定节点数的 对称并且完全平衡 的所有二叉树
  def symmetricBalancedTrees[T](numOfNodes: Int, value: T): List[Node[T]] = {
    makeTree_byNodes(numOfNodes, value).filter(x => x.isCompleteBalance && x.isSymmetric).toList
  }

  // 给定节点数，构造完全二叉树
  def completeBinaryTree[T](numOfNodes: Int, value: T): Tree[T] = {
    def add1(node: Tree[T]): Tree[T] = node match {
      case End => Node(value)
      case Node(v, l, r) if l.size == r.size => Node(v, add1(l), r)
      case Node(v, l, r) if l.size > r.size && l.isFull => Node(v, l, add1(r))
      case Node(v, l, r) if l.size > r.size => Node(v, add1(l), r)
    }
    def add(node: Tree[T], num: Int): Tree[T] = num match {
      case 0 => node
      case n if n > 0 =>
        val newNode = add1(node)
        add(newNode, n - 1)
    }
    if (numOfNodes <= 0) End else add(Node(value), numOfNodes - 1)
  }

  // 构造给定 height 的所有 height-balanced binary tree
  // 定义： 对任意节点，左右树的 height 最多相差1
  def hbalTrees[T](height: Int, value: T): List[Node[T]] = height match {
    case 1 => List(Node(value))
    case 2 => List(Node(value, Node(value), End),
      Node(value, End, Node(value)),
      Node(value, Node(value), Node(value)))
    case n if n > 2 =>
      val n_1 = hbalTrees(n - 1, value)
      val n_2 = hbalTrees(n - 2, value)
      n_1.flatMap(n1 => n_2.map(n2 => Node(value, n1, n2))) :::
        n_1.flatMap(n1 => n_2.map(n2 => Node(value, n2, n1))) :::
        n_1.flatMap(n1 => n_1.map(n11 => Node(value, n1, n11)))
  }

  // 指定height的所有height-balanced二叉树的总数
  private val f2 = new Function1[Int, Int] {
    def apply(height: Int): Int = height match {
      case 0 => 1
      case 1 => 1
      case i if i > 1 => 2 * this(i - 1) * this(i - 2) + this(i - 1) * this(i - 1)
    }
  }


  private def hbalTreesWithNodes1[T](numOfNodes: Int, value: T): List[Node[T]] =
    makeTree_byNodes(numOfNodes, value).filter(_.isHeightBalance).toList

  private val height: Stream[Int] = 0 #:: height.map(_ + 1)
  private val maxNodesOfHeight: Stream[Int] = height.map(h => if (h > 0) Math.pow(2, h).toInt - 1 else 0)

  // 构造给定节点数的所有 height-balanced binary tree
  // 定义： 对任意节点，左右树的 height 最多相差1
  def hbalTreesWithNodes[T](nodesNum: Int, value: T): List[Tree[T]] = {
    val maxh = maxHbalHeight(nodesNum)
    val minh = maxNodesOfHeight.takeWhile(_ < nodesNum).length
    (minh to maxh).toList.flatMap(hbalTrees(_, value)).filter(_.size == nodesNum)
  }

  val minHbalNodes: Int => Int = {
    case 0 => 0
    case 1 => 1
    case n if n > 1 => minHbalNodes(n - 1) + 1 + minHbalNodes(n - 2)
  }

  val maxHbalHeight: Int => Int = {
    nodesNum =>
      def ls: Stream[Int] = 0 #:: 1 #:: ls.zip(ls.drop(1)).map(x => x._1 + x._2 + 1)
      ls.takeWhile(_ <= nodesNum).length - 1
  }

  def fromString(s: String): Node[Char] = Node('x')

//  def fromDotstring(s: String): Node[Char] = Node('x')

  // 给定一个二叉树，构造出增加一个节点后所有可能的二叉树
  private def add1Node[T](v: T, node: Node[T]): List[Node[T]] = {
    def add(node: Tree[T]): List[Node[T]] = node match {
      case n@End => List(Node(v))
      case n@Node(_, left: Tree[_], right: Tree[_]) =>
        add(left).map(Node(v, _, right)) ::: add(right).map(Node(v, left, _))
    }
    add(node)
  }

  // 构造出给定 height 的所有二叉树。  有严重效率问题
  private def makeTree_byHeight[T](height: Int, v: T): Set[Node[T]] = {
    (1 to Math.pow(2, height).toInt - 1).foldLeft(Set[Node[T]]()) {
      (r, e) => r ++ makeTree_byNodes(e, v).filter(_.height() == height)
    }
  }

  // 构造出给定节点数的所有二叉树，效率？ 阶乘级别复杂度。。。。。
  private def makeTree_byNodes[T](num: Int, v: T): Set[Node[T]] = {
    (1 to num - 1).foldLeft(Set(Node(v))) {
      (rerult, _) => rerult.flatMap(add1Node(v, _))
    }
  }

}