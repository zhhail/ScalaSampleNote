package com.zte.bigdata

import org.apache.hadoop.hive.ql.exec.UDFArgumentException
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF
import org.apache.hadoop.hive.serde2.objectinspector.{ListObjectInspector, ObjectInspector, ObjectInspectorFactory, StructObjectInspector}

import scala.collection.JavaConversions._
import scala.util.Try

class MRSConvert extends GenericUDTF {
  private val vs: Vector[Int] = Vector(-1260, -1255, -1245, -1235, -1225, -1215, -1205, -1195, -1185, -1175, -1165, -1155, -1145, -1135, -1125, -1115, -1105, -1095, -1085, -1075, -1065, -1055, -1045, -1035, -1025, -1015, -1005, -995, -985, -975, -965, -955, -945, -935, -925, -915, -905, -895, -885, -875, -865, -855, -845, -835, -825, -815, -805, -795, -785, -775, -765, -755, -750)
  private var inputOIs: Array[ObjectInspector] = _

  //  输入 53 个 长度为 100 的数组
  override def process(args: Array[AnyRef]) = {
    if (args.length != 53) throw new UDFArgumentException(s"only support 53 input columns, input: ${args.length}")
    lazy val in = args.zip(inputOIs).map {
      case (arg, meta) =>
        meta.asInstanceOf[ListObjectInspector].getList(arg).map {
          case x: Integer => x
          case null => null
        }.toVector
    }.toVector
    val res: Array[Integer]=Try(transpose(in).map(cacl).toArray).getOrElse(Array())
    forward(res)
  }

  private def cacl(in: IndexedSeq[Integer]): Integer = Try {
    val total = in.zip(vs).map { case (n, v) => n * v }.sum
    val num = in.map(_.toInt).sum * 10
    new Integer(total / num)
  }.getOrElse(null)

  private def transpose[A](ls: IndexedSeq[IndexedSeq[A]]): IndexedSeq[IndexedSeq[A]] = {
    require(!ls.map(_.length == 100).toSet.contains(false), f"各元素长度不一致，必须全部为100。 size of ripprb(${ls.zipWithIndex.filter(_._1.length != 100).map(_._2).mkString(",")} 不为100")
    for (j <- ls.head.indices) yield for (i <- ls.indices) yield ls(i)(j)
  }

  override def initialize(argOIs: Array[ObjectInspector]): StructObjectInspector = {
    inputOIs = argOIs
    val fieldNames = (0 to 99).map(x => s"col${x + 1}")
    val fieldOIs = fieldNames.map(_ => argOIs.head.asInstanceOf[ListObjectInspector].getListElementObjectInspector)
    ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames, fieldOIs)
  }

  override def close() = {}
}
