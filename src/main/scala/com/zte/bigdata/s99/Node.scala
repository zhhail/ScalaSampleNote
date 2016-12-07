package com.zte.bigdata.s99

class Node[+T](val value: T, val left: Tree[T], val right: Tree[T]) extends Tree[T] {
  def this(value: T) = this(value, End, End)

  override def toString = this match {
    case Node(v, End, End) => v.toString
    case Node(v, l, End) => s"$v($l,)"
    case Node(v, End, r) => s"$v(,$r)"
    case Node(v, l, r) => s"$v($l,$r)"
  }

  override def toDotstring = s"$value${left.toDotstring}${right.toDotstring}"

  override def equals(that: Any): Boolean = that match {
    case Node(v, l, r) => value == v && left == l && right == r
    case _ => false
  }

  override def hashCode: Int = size * 10000 + left.size * 100 + right.size

  override def addValue[U >: T <% Ordered[U]](x: U): Node[U] = x match {
    case v if v > value => Node(value, left, right.addValue(v))
    case v if v < value => Node(value, left.addValue(v), right)
  }

  def layoutBinaryTree: PositionedNode[T] = {
    def layout[U](node: Node[U], x: Int, y: Int): PositionedNode[U] = node match {
      case Node(v, End, End) => PositionedNode(PositionedValue(v, x, y), End, End)
      case Node(v, l: Node[U], End) => PositionedNode(PositionedValue(v, l.size + x, y), layout(l, x, y + 1), End)
      case Node(v, End, r: Node[U]) => PositionedNode(PositionedValue(v, x, y), End, layout(r, x + 1, y + 1))
      case Node(v, l: Node[U], r: Node[U]) => PositionedNode(
        PositionedValue(v, l.size + x, y),
        layout(l, x, y + 1),
        layout(r, l.size + x + 1, y + 1))
    }
    layout(this, 1, 1)
  }

  def layoutBinaryTree2: PositionedNode[T] = {
    def layout(node: Node[T], x: Int, y: Int, hLeft: Int): PositionedNode[T] = node match {
      case Node(v, End, End) => PositionedNode(PositionedValue(v, x + Math.pow(2, hLeft).toInt, y), End, End)
      case Node(v, l: Node[T], End) => PositionedNode(PositionedValue(v, x + Math.pow(2, hLeft).toInt, y),
        layout(l, x, y + 1, hLeft - 1),
        End)
      case Node(v, End, r: Node[T]) => PositionedNode(PositionedValue(v, x + Math.pow(2, hLeft).toInt, y),
        End,
        layout(r, x + Math.pow(2, hLeft).toInt, y + 1, hLeft - 1))
      case Node(v, l: Node[T], r: Node[T]) => PositionedNode(PositionedValue(v, x + Math.pow(2, hLeft).toInt, y),
        layout(l, x, y + 1, hLeft - 1),
        layout(r, x + Math.pow(2, hLeft).toInt, y + 1, hLeft - 1))
    }
    layout(this, 0, 1, height - 1)
  }

  def layoutBinaryTree3: PositionedNode[T] = ???

  @deprecated
  private def all_left(): String = {
    def left[U](node: Tree[U]): String = node match {
      case e if e == End => "."
      case Node(_, l: Tree[_], r: Tree[_]) => s"N(${left(l)}x${left(r)})"
    }
    left(this)
  }

  @deprecated
  private def all_right(): String = {
    def right[U](node: Tree[U]): String = node match {
      case e if e == End => "."
      case Node(_, l: Tree[_], r: Tree[_]) => s"N(${right(r)}x${right(l)})"
    }
    right(this)
  }

  override def isSymmetric = all_right == all_left

  override def isCompleteBalance: Boolean =
    Math.abs(left.size - right.size) <= 1 && left.isCompleteBalance && right.isCompleteBalance

  override def size: Int = left.size + 1 + right.size

  override def height: Int = Math.max(left.height, right.height) + 1

  //  定义：对每一个节点，left和right的height相差不超过1
  override def isHeightBalance: Boolean =
    Math.abs(left.height - right.height) <= 1 && left.isHeightBalance && right.isHeightBalance

  //  是否满二叉树
  override def isFull: Boolean = left.isFull && right.isFull && left.size == right.size

  def leafCount(): Int = leafList.length

  override def leafList: List[T] = this match {
    case Node(v, End, End) => List(v)
    case Node(v, l, r) => l.leafList ::: r.leafList
  }

  override def internalList: List[T] = this match {
    case Node(v, End, End) => Nil
    case Node(v, l, r) => l.internalList ::: (v :: r.internalList)
  }

  override def atLevel(level: Int): List[T] = level match {
    case 1 => List(value)
    case n if n > 1 => left.atLevel(n - 1) ::: right.atLevel(n - 1)
  }

  override def inorder: List[T] = allNode {
    node: Node[T] => node.left.inorder ::: (node.value :: node.right.inorder)
  }

  override def preorder: List[T] = allNode {
    node: Node[T] => (node.value :: node.left.preorder) ::: node.right.preorder
  }

  override def postorder: List[T] = allNode {
    node: Node[T] => node.left.postorder ::: node.right.postorder ::: List(node.value)
  }

  private def allNode[U](f: Node[U] => List[U]): List[U] = this match {
    case n if n == End => Nil
    case n: Node[U] => f(n)
  }
}

case object End extends Tree[Nothing] {
  override def toString = "."

  override def toDotstring = "."

  override def addValue[U <% Ordered[U]](x: U): Tree[U] = Node(x)

  override def size = 0

  override def height: Int = 0

  override def isFull: Boolean = true

  override def isCompleteBalance: Boolean = true

  override def isHeightBalance: Boolean = true

  override def leafList: List[Nothing] = Nil

  override def internalList: List[Nothing] = Nil

  override def atLevel(level: Int): List[Nothing] = Nil

  override def preorder: List[Nothing] = Nil

  override def inorder: List[Nothing] = Nil

  override def postorder: List[Nothing] = Nil

}

object Node {
  def unapply[T](node: Node[T]) = Some(node.value, node.left, node.right)

  def apply[T](value: T, left: Tree[T], right: Tree[T]): Node[T] = new Node(value, left, right)

  def apply[T](value: T): Node[T] = new Node(value)

}

