package com.zte.bigdata

import org.apache.hadoop.hive.ql.exec.UDFArgumentException
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF
import org.apache.hadoop.hive.serde2.objectinspector.primitive._
import org.apache.hadoop.hive.serde2.objectinspector.{ListObjectInspector, ObjectInspector, ObjectInspectorFactory, StructObjectInspector}

import scala.collection.JavaConversions._

class NeighborExplode_out extends GenericUDTF {

  private var inputOIs: Array[ObjectInspector] = _
  private var inputNumInspector: ObjectInspector = _

  override def initialize(argOIs: Array[ObjectInspector]): StructObjectInspector = {
    inputOIs = argOIs.tail
    inputNumInspector = argOIs.head match {
      case x: ByteObjectInspector => x
      case x: ShortObjectInspector => x
      case x: IntObjectInspector => x
      case _ => throw new UDFArgumentException(s"invalid num type: ${argOIs.head.getTypeName}")
    }
    val fieldNames: List[String] = inputOIs.indices.map(x => s"col${x + 1}").toList
    val fieldOIs: List[ObjectInspector] = inputOIs.map {
      case x: ListObjectInspector => x.getListElementObjectInspector
      case y => y
    }.toList
    ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames, fieldOIs)
  }

  override def process(args: Array[AnyRef]): Unit = {
    var num: Int = if (args.head == null) 0 else
      inputNumInspector match {
      case x: ByteObjectInspector => x.get(args(0))
      case x: ShortObjectInspector => x.get(args(0))
      case x: IntObjectInspector => x.get(args(0))
    }

    if(num>50) num=50
    if (num == 0) forward(args.tail.indices.map(_ => null).toArray)
    else {
      val rr = inputOIs.zip(args.tail).map {
        case (meta, data) if meta.isInstanceOf[ListObjectInspector] && data != null => meta.asInstanceOf[ListObjectInspector].getList(data).toArray
        case _ => Array()
      }.map(x => x.padTo(num, null))
      for (i <- 0 until num) {
        forward(rr.map(_ (i)))
      }
    }
  }

  override def close(): Unit = {}
}
