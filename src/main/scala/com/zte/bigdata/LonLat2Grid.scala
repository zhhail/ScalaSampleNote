package com.zte.bigdata

import java.lang.Double

import com.zte.bigdata.udf.coordinate2Grid.{GridCoordinateGetter, GridOffSetData}
import org.apache.hadoop.hive.common.`type`.HiveDecimal
import org.apache.hadoop.hive.ql.exec.UDFArgumentException
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF
import org.apache.hadoop.hive.serde2.objectinspector.primitive._
import org.apache.hadoop.hive.serde2.objectinspector.{ObjectInspector, ObjectInspectorFactory, PrimitiveObjectInspector, StructObjectInspector}

import scala.collection.JavaConversions._

class LonLat2Grid extends GenericUDTF {
  private var inputOIs: Array[PrimitiveObjectInspector] = _

  override def initialize(argOIs: Array[ObjectInspector]): StructObjectInspector = {
    if (argOIs.length != 3) throw new UDFArgumentException(s"need 3 args: lon,lat,gridsize!")
    inputOIs = argOIs.map {
      _.asInstanceOf[PrimitiveObjectInspector]
    }

    val fieldNames: List[String] = List("regionid", "x_offset", "y_offset")
    val fieldOIs: List[ObjectInspector] = List(
      PrimitiveObjectInspectorFactory.javaIntObjectInspector,
      PrimitiveObjectInspectorFactory.javaIntObjectInspector,
      PrimitiveObjectInspectorFactory.javaIntObjectInspector)
    ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames, fieldOIs)
  }

  override def process(objects: Array[AnyRef]): Unit = if (objects.contains(null)) forward(Array(null, null, null))
  else {
    val lon: Double = inputOIs(0).getPrimitiveJavaObject(objects(0)) match {
      case x: Number => x.doubleValue()
      case x: HiveDecimal => x.doubleValue()
      case _ => null
    }
    val lat: Double = inputOIs(1).getPrimitiveJavaObject(objects(1)) match {
      case x: Number => x.doubleValue()
      case x: HiveDecimal => x.doubleValue()
      case _ => null
    }
    val gridSize: Double = inputOIs(2).getPrimitiveJavaObject(objects(2)) match {
      case x: Number => x.doubleValue()
      case x: HiveDecimal => x.doubleValue()
      case _ => null
    }
    if (lon != null && lat != null && gridSize != null) {
      val grid = GridCoordinateGetter.getGridId(lon, lat, gridSize)
      forward(Array(new Integer(grid.earthID), new Integer(grid.xOffSet), new Integer(grid.yOffSet)))
    }
    else forward(Array(null, null, null))
  }

  override def close(): Unit = {}
}
