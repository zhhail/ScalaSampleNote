package com.zte.bigdata.s99

class Node[+T](val value: T, val left: Tree[T], val right: Tree[T]) extends Tree[T] {
  def this(value: T) = this(value, End, End)

  override def toString = this match {
    case Node(v, End, End) => v.toString
    case Node(v, l, End) => s"$v($l,)"
    case Node(v, End, r) => s"$v(,$r)"
    case Node(v, l, r) => s"$v($l,$r)"
  }

  override def equals(that: Any): Boolean = that match {
//    case t: Node[T] => value == t.value && left == t.left && right == t.right
    case Node(v,l,r) => value == v && left == l && right == r
    case _ => false
  }

  override def hashCode: Int = size() * 10000 + left.size() * 100 + right.size()

  override def addValue[U >: T <% Ordered[U]](x: U): Node[U] = x match {
    case v if v > value => Node(value, left, right.addValue(v))
    case v if v < value => Node(value, left.addValue(v), right)
  }

  def layoutBinaryTree: PositionedNode[T] = {
    def layout[U](node: Node[U], x: Int, y: Int): PositionedNode[U] = node match {
      case Node(v, End, End) => PositionedNode(PositionedValue(v, x, y), End, End)
      case Node(v, l: Node[U], End) => PositionedNode(PositionedValue(v, l.size() + x, y), layout(l, x, y + 1), End)
      case Node(v, End, r: Node[U]) => PositionedNode(PositionedValue(v, x, y), End, layout(r, x + 1, y + 1))
      case Node(v, l: Node[U], r: Node[U]) => PositionedNode(
        PositionedValue(v, l.size() + x, y),
        layout(l, x, y + 1),
        layout(r, l.size() + x + 1, y + 1))
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

  override def size(): Int = left.size() + 1 + right.size()

  override def height(): Int = Math.max(left.height(), right.height()) + 1

  def isHeightBalance: Boolean = {
    def heightBalance(node: Node[T]): Boolean = node match {
      case Node(_, End, End) => true
      case Node(_, left1: Tree[T], right1: Tree[T]) => Math.abs(left1.height - right1.height) <= 1 && left1.isHeightBalance && right1.isHeightBalance
    }
    heightBalance(this)
  }

  override def isFull: Boolean = this match {
    case n if n == End => true
    case Node(_, l, r) => l.isFull && r.isFull && l.size == r.size
  }

  def leafCount(): Int = leafList.length

  def leafList: List[T] = {
    def getleafs(node: Node[T]): List[T] = node match {
      case Node(_, End, End) => List(node.value)
      case Node(_, left1: Node[T], End) => getleafs(left1)
      case Node(_, End, right1: Node[T]) => getleafs(right1)
      case Node(_, left1: Node[T], right1: Node[T]) => getleafs(left1) ::: getleafs(right1)
    }
    getleafs(this)
  }

  def internalList: List[T] = {
    def getInternal(node: Node[T]): List[T] = node match {
      case Node(_, End, End) => List()
      case Node(v, left1: Node[T], End) => v :: getInternal(left1)
      case Node(v, End, right1: Node[T]) => v :: getInternal(right1)
      case Node(v, left1: Node[T], right1: Node[T]) => v :: getInternal(left1) ::: getInternal(right1)
    }
    getInternal(this)
  }

  override def atLevel(level: Int): List[T] = level match {
    case 1 => List(value)
    case n if n > 1 => left.atLevel(n - 1) ::: right.atLevel(n - 1)
  }

  def all_inorder:String = Node.all_inorder(this)
  def all_preorder:String = Node.all_preorder(this)
  def all_postorder:String = Node.all_postorder(this)

  @deprecated
  // use all(order: Order = MIDDLE) instead.
  def allNode(): String = {
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
    middleOrder(this)
  }
}

case object End extends Tree[Nothing] {
  override def toString = "."

  override def addValue[U <% Ordered[U]](x: U): Tree[U] = Node(x)

  override def size() = 0

  override def height(): Int = 0

  override def isFull: Boolean = true

  override def isCompleteBalance: Boolean = true

  override def isHeightBalance: Boolean = true

  override def atLevel(level: Int): List[Nothing] = Nil
}

object Node {
  def unapply[T](node: Node[T]) = Some(node.value, node.left, node.right)

  def apply[T](value: T, left: Tree[T], right: Tree[T]): Node[T] = new Node(value, left, right)

  def apply[T](value: T): Node[T] = new Node(value)

  private def forall[T](node: Tree[T])(f: Node[T] => String): String = node match {
    case n if n == End => ""
    case n: Node[T] => f(n)
  }

  private def all_inorder[T](node: Tree[T]): String = Node.forall(node) {
    node => all_inorder(node.left) + s"(${node.value})" + all_inorder(node.right)
  }

  private def all_preorder[T](node: Tree[T]): String = Node.forall(node) {
    node => s"(${node.value})" + all_preorder(node.left) + all_preorder(node.right)
  }

  private def all_postorder[T](node: Tree[T]): String = Node.forall(node) {
    node => all_postorder(node.left) + all_postorder(node.right) + s"(${node.value})"
  }

}

