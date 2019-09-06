package com.zte.bigdata

import com.zte.bigdata.udf.coordinate2Grid.GridCoordinateGetter
import org.apache.hadoop.hive.ql.exec.UDFArgumentTypeException
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF
import org.apache.hadoop.hive.serde2.objectinspector._
import org.apache.hadoop.hive.serde2.objectinspector.primitive._

import scala.collection.JavaConversions._
import scala.util.Try

class Coordinate2Grid extends GenericUDF {
  override def getDisplayString(children: Array[String]): String = {
    "GenericUDF: Coordinate2Grid, transform coodinate to grid!!! "
  }

  private var inputOIs: Array[ObjectInspector] = _

  override def initialize(paras: Array[ObjectInspector]): ObjectInspector = {
    if (paras.length != 2 && paras.length != 3) throw new UDFArgumentTypeException(paras.length - 1, "Exactly two or three argument is expected.")
    inputOIs = paras

    paras(0) match {
      case _: DoubleObjectInspector =>
      case e => throw new UDFArgumentTypeException(0, s"${e.getTypeName} not DoubleObjectInspector")
    }
    paras(1) match {
      case _: DoubleObjectInspector =>
      case e => throw new UDFArgumentTypeException(1, s"${e.getTypeName} not DoubleObjectInspector")
    }
    if (paras.length == 3) paras(2) match {
      case _: IntObjectInspector =>
      case e => throw new UDFArgumentTypeException(2, s"${e.getTypeName} not IntObjectInspector")
    }

    ObjectInspectorFactory.getStandardStructObjectInspector(List("regionid", "x_offset", "y_offset"),
      List(PrimitiveObjectInspectorFactory.javaIntObjectInspector, PrimitiveObjectInspectorFactory.javaIntObjectInspector, PrimitiveObjectInspectorFactory.javaIntObjectInspector))
  }

  override def evaluate(arguments: Array[GenericUDF.DeferredObject]): AnyRef = Try {
    val lon: java.lang.Double = inputOIs(0).asInstanceOf[PrimitiveObjectInspector].getPrimitiveJavaObject(arguments(0).get).asInstanceOf[Number].doubleValue()
    val lat: java.lang.Double = inputOIs(1).asInstanceOf[PrimitiveObjectInspector].getPrimitiveJavaObject(arguments(1).get).asInstanceOf[Number].doubleValue()
    val gSize: Int = if (inputOIs.length == 3) {
      inputOIs(2).asInstanceOf[PrimitiveObjectInspector].getPrimitiveJavaObject(arguments(2).get).asInstanceOf[Number].intValue()
    }
    else 1

    if (lon != null && lat != null && Math.abs(lon) <= 180.0 && Math.abs(lat) <= 90.0) {
      val res = GridCoordinateGetter.getGridId(lon, lat, gSize)
      Array(new Integer(res.earthID), new Integer(res.xOffSet), new Integer(res.yOffSet))
    }
    else Array(null, null, null)
  }.getOrElse(Array(null, null, null))
}
