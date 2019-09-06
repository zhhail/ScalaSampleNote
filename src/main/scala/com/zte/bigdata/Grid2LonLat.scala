package com.zte.bigdata

import java.lang.{Double, Integer => Int}

import com.zte.bigdata.udf.coordinate2Grid.GridCoordinateGetter
import org.apache.hadoop.hive.ql.exec.UDFArgumentException
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory
import org.apache.hadoop.hive.serde2.objectinspector.{ObjectInspector, ObjectInspectorFactory, PrimitiveObjectInspector, StructObjectInspector}

import scala.collection.JavaConversions._

class Grid2LonLat extends GenericUDTF {
  private var inputOIs: Array[PrimitiveObjectInspector] = _

  override def initialize(argOIs: Array[ObjectInspector]): StructObjectInspector = {
    if (argOIs.length != 4) throw new UDFArgumentException(s"need 4 args: earthid,x,y,gridsize!")
    inputOIs = argOIs.map {
      _.asInstanceOf[PrimitiveObjectInspector]
    }

    val fieldNames: List[String] = List("lon", "lat")
    val fieldOIs: List[ObjectInspector] = List(
      PrimitiveObjectInspectorFactory.javaDoubleObjectInspector,
      PrimitiveObjectInspectorFactory.javaDoubleObjectInspector)
    ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames, fieldOIs)
  }

  override def process(objects: Array[AnyRef]): Unit = if (objects.contains(null)) forward(Array(null, null))
  else {
    val earthid: Int = inputOIs(0).getPrimitiveJavaObject(objects(0)) match {
      case x: Number => x.intValue()
      case _ => null
    }
    val x: Int = inputOIs(1).getPrimitiveJavaObject(objects(1)) match {
      case x: Number => x.intValue()
      case _ => null
    }
    val y: Int = inputOIs(2).getPrimitiveJavaObject(objects(2)) match {
      case x: Number => x.intValue()
      case _ => null
    }
    val gridSize: Int = inputOIs(3).getPrimitiveJavaObject(objects(3)) match {
      case x: Number => x.intValue()
      case _ => null
    }

    if (earthid != null && x != null && y != null && gridSize != null) {
      val grid = GridCoordinateGetter.getGridCenterCoord(earthid, x, y, gridSize)
      forward(Array(new Double(grid.centerLon), new Double(grid.centerLat)))
    }
    else  forward(Array(null, null))
  }

  override def close(): Unit = {}
}

