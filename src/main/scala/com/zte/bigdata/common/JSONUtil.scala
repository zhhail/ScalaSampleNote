package com.zte.bigdata.common

import org.json4s._
import org.json4s.native.Serialization
import scala.util.Try
import com.zte.bigdata.xmlreader.common.MROConfigPara

trait JSONUtil {
  implicit val formats: Formats = DefaultFormats + FieldSerializer[MROConfigPara]()

  def toJSON(objectToWrite: AnyRef): String = Serialization.write(objectToWrite)

  def fromJSONOption[T](jsonString: String)(implicit mf: Manifest[T]): Option[T] = Try(Serialization.read(jsonString)).toOption

}
