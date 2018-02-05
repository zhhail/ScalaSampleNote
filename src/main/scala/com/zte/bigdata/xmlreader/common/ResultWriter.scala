package com.zte.bigdata.xmlreader.common

import java.io.Writer

trait ResultWriter extends ResultWriter_File

sealed trait ResultWriter_Base {
  def lineWriter(writer: Writer, line: String): Unit
}

trait ResultWriter_File extends ResultWriter_Base {
  override def lineWriter(writer: Writer, line: String): Unit = {
    writer.write(line)
  }
}

trait ResultWriter_None extends ResultWriter_Base {
  override def lineWriter(writer: Writer, line: String): Unit = {}
}

