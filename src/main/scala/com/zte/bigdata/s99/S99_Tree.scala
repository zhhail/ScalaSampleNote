package com.zte.bigdata.s99

sealed abstract class Order

case object MIDDLE extends Order

case object FORWARD extends Order

case object REVERSE extends Order

sealed trait Tree[+T] {
  def size(): Int

  def isSymmetric: Boolean = true

  def addValue[U >: T <% Ordered[U]](x: U): Tree[U]

  def isCompleteBalance: Boolean
}

class Node[+T](val value: T, val left: Tree[T], val right: Tree[T]) extends Tree[T] {
  def this(value: T) = this(value, End, End)

  override def toString = "T(" + value.toString + ", " + left.toString + ", " + right.toString + ")"

  override def equals(that: Any): Boolean = that match {
    case t: Node[T] => value == t.value && left == t.left && right == t.right
    case _ => false
  }

  override def hashCode: Int = size() * 10000 + left.size() * 100 + right.size()

  override def addValue[U >: T <% Ordered[U]](x: U): Tree[U] = x match {
    case v if v > value => Node(value, left, right.addValue(v))
    case v if v < value => Node(value, left.addValue(v), right)
  }

  private def all_left(): String = {
    def left[U](node: Tree[U]): String = node match {
      case e if e == End => "#"
      case Node(_, l: Tree[_], r: Tree[_]) => s"N(${left(l)}x${left(r)})"
    }
    left(this)
  }

  private def all_right(): String = {
    def right[U](node: Tree[U]): String = node match {
      case e if e == End => "#"
      case Node(_, l: Tree[_], r: Tree[_]) => s"N(${right(r)}x${right(l)})"
    }
    right(this)
  }

  override def isSymmetric = all_right == all_left

  def isCompleteBalance: Boolean = {
    def completeBalance(node: Node[T]): Boolean = node match {
      case Node(_, End, End) => true
      case Node(_, left1: Tree[T], right1: Tree[T]) => Math.abs(left1.size() - right1.size) <= 1 && left1.isCompleteBalance && right1.isCompleteBalance
    }
    completeBalance(this)
  }

  def size(): Int = left.size() + 1 + right.size()

  def leafs(order: Order = MIDDLE): List[String] = {
    def getleafs(node: Node[T]): List[String] = node match {
      case Node(_, End, End) => List(node.value.toString)
      case Node(_, left1: Node[T], End) => getleafs(left1)
      case Node(_, End, right1: Node[T]) => getleafs(right1)
      case Node(_, left1: Node[T], right1: Node[T]) => getleafs(left1) ::: getleafs(right1)
    }
    getleafs(this)
  }

  def all(order: Order = MIDDLE): String = {
    def forall(node: Tree[T])(f: Node[T] => String): String = node match {
      case n if n == End => ""
      case n: Node[T] => f(n)
    }
    def middle(node: Tree[T]): String = forall(node)(node => middle(node.left) + s"(${node.value})" + middle(node.right))
    def forward(node: Tree[T]): String = forall(node)(node => s"(${node.value})" + forward(node.left) + forward(node.right))
    def reverse(node: Tree[T]): String = forall(node)(node => reverse(node.left) + reverse(node.right) + s"(${node.value})")
    order match {
      case MIDDLE => middle(this)
      case FORWARD => forward(this)
      case REVERSE => reverse(this)
    }
  }

  @deprecated
  // use all(order: Order = MIDDLE) instead.
  def allNode(order: Order = MIDDLE): String = {
    def middleOrder(node: Tree[T]): String = node match {
      case n if n == End => ""
      case Node(v, left1: Tree[T], right1: Tree[T]) =>
        middleOrder(left1) + s"($v)" + middleOrder(right1)
    }
    def forwardOrder(node: Tree[T]): String = node match {
      case n if n == End => ""
      case Node(v, left1: Tree[T], right1: Tree[T]) =>
        s"($v)" + forwardOrder(left1) + forwardOrder(right1)
    }
    def reverseOrder(node: Tree[T]): String = node match {
      case n if n == End => ""
      case Node(v, left1: Tree[T], right1: Tree[T]) =>
        reverseOrder(left1) + reverseOrder(right1) + s"($v)"
    }

    order match {
      case MIDDLE => middleOrder(this)
      case FORWARD => forwardOrder(this)
      case REVERSE => reverseOrder(this)
    }
  }

}

case object End extends Tree[Nothing] {
  override def toString = "."

  override def addValue[U <% Ordered[U]](x: U): Tree[U] = Node(x)

  def size() = 0

  def isCompleteBalance: Boolean = true
}

object Tree {
  //  从一个list构造binary search tree.
  def fromList[U <% Ordered[U]](ls: List[U]): Tree[U] =
    ls.foldLeft(End: Tree[U])((r, e) => r.addValue(e))

  // 构造给定节点数的所有 completely balanced binary tree.
  // 定义： 对任意节点，左右树的节点数最多相差1个
  def cBalanced[T](numOfNodes: Int, value: T): List[Node[T]] =
    makeTree(numOfNodes, value).filter(_.isCompleteBalance).toList

  // 构造给定节点数的所有 height-balanced binary tree
  // 定义： 对任意节点，左右树的 height 最多相差1

  def symmetricBalancedTrees[T](numOfNodes: Int, value: T): List[Node[T]] = {
    makeTree(numOfNodes, value).filter(x => x.isCompleteBalance && x.isSymmetric).toList
  }

  // 给定一个二叉树，构造出增加一个节点后所有可能的二叉树
  private def add1Node[T](v: T, node: Node[T]): List[Node[T]] = {
    def add(node: Tree[T]): List[Node[T]] = node match {
      case n@End => List(Node(v))
      case n@Node(_, left: Tree[_], right: Tree[_]) =>
        add(left).map(Node(v, _, right)) ::: add(right).map(Node(v, left, _))
    }
    add(node)
  }

  // 构造出给定节点数的所有二叉树
  private def makeTree[T](num: Int, v: T): Set[Node[T]] = {
    //      var result = List(Node(v))
    //      for (i <- 1 to num - 1) {
    //        result = result.flatMap(add1Node(v, _))
    //      }
    //      result
    (1 to num - 1).foldLeft(Set(Node(v))) {
      (rerult, _) => rerult.flatMap(add1Node(v, _))
    }
  }


}

object Node {
  def unapply[T](node: Node[T]) = Some(node.value, node.left, node.right)

  def apply[T](value: T, left: Tree[T], right: Tree[T]): Node[T] = new Node(value, left, right)

  def apply[T](value: T): Node[T] = new Node(value)

}

