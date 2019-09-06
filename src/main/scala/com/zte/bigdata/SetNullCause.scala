package com.zte.bigdata

import org.apache.hadoop.hive.ql.exec.UDFArgumentException
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF
import org.apache.hadoop.hive.serde2.objectinspector._

import scala.collection.JavaConversions._

class SetNullCause extends GenericUDTF {
  private var inputOIs: Array[ObjectInspector] = _

  override def process(args: Array[AnyRef]) =
    if (args(0) == null || args(1) == null) {
      forward(Array(null, null))
    }
    else {
      val asv1 = inputOIs(0).asInstanceOf[ListObjectInspector].getList(args(0)).toArray
      val asv2 = inputOIs(1).asInstanceOf[ListObjectInspector].getList(args(1)).toArray

      val v1 = inputOIs(2) match {
        case x: PrimitiveObjectInspector => x.getPrimitiveJavaObject(args(2))
      }
      val v2 = inputOIs(3) match {
        case x: PrimitiveObjectInspector => x.getPrimitiveJavaObject(args(3))
      }

      val (r1, r2) = asv1.zip(asv2).map {
        case (a, b) if a == null || b == null => (v1, v2)
        case x => x
      }.unzip
      forward(Array(r1, r2))
    }

  override def initialize(argOIs: Array[ObjectInspector]): StructObjectInspector = {
    //    TDB
    def checkSpector(e: Any, ls: Any): Boolean = true

    //    val type1 = e
    //    val type2 = ls.asInstanceOf[ListObjectInspector].getListElementObjectInspector

    //    参数个数检查
    if (argOIs.length != 4) throw new UDFArgumentException(s"only support 4 input columns, input: ${argOIs.length}")
    //    参数类型检查
    Array(0, 1).foreach {
      idx =>
        if (!argOIs(idx).isInstanceOf[ListObjectInspector])
          throw new UDFArgumentException(s"the $idx th input must be array type!")
    }
    Array(2, 3).foreach {
      idx =>
        if (!checkSpector(argOIs(idx), argOIs(idx - 2)))
          throw new UDFArgumentException(s"type of $idx th input must same as the element of input ${idx - 2} th!")
    }
    inputOIs = argOIs
    val fieldNames: List[String] = List("col1", "col2")
    val out1 = ObjectInspectorFactory.getStandardListObjectInspector(argOIs(0).asInstanceOf[ListObjectInspector].getListElementObjectInspector)
    val out2 = ObjectInspectorFactory.getStandardListObjectInspector(argOIs(1).asInstanceOf[ListObjectInspector].getListElementObjectInspector)
    val fieldOIs = List(out1, out2)

    ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames, fieldOIs)
  }

  override def close() = {}
}

/* 测试验证
create TEMPORARY FUNCTION setnullv as 'com.zte.bigdata.SetNullCause';
select setnullv(a,b,-333,-444),a,b from (select array(1,2,null,3,null) as a, array(2,9,4,null,null) as b);
*/