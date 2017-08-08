package com.zte.bigdata.common

import com.typesafe.config.{ConfigFactory, Config}
import java.io.File

trait ConfigSupport extends LogSupport {

  /**
   * 配置文件，只写文件名即可。
   *
   * 优先读取configHome路径下的文件，同时如果resources下有同名文件也会读取   *
   * 注意：在前面的文件会覆盖后面的文件的相同key的配置值
   *
   */
  val configFiles: Vector[String]

  protected var configHome = "./conf/"

  protected lazy val config: Config = {
    val conf_default = loadDefault(configFiles)
    val conf_userdef = parseUserDef(configFiles.map(x => configHome + "/" + x))
    conf_userdef.withFallback(conf_default)
  }

  private def getAll(files:Seq[String])(f:String=>Config):Config = files.map(f).reduce(_.withFallback(_))

  private def loadDefault(files: Seq[String]) = getAll(files)(ConfigFactory.load)
  private def parseUserDef(files: Seq[String]) = getAll(files)(x=>ConfigFactory.parseFile(new File(x)))

}
