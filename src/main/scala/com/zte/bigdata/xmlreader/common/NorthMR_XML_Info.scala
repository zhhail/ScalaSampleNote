package com.zte.bigdata.xmlreader.common

import java.io.OutputStreamWriter

trait NorthMR_XML_Info {
  //  DateTime
  //  MilliSec
  //  Network
  //  eNodeBID
  //  CID
  //  UEGid
  //  TraceID
  //  CallRecordUEID
  //  IMSI
  //  DataType
  //  UeUlSinr
  //  UePhr
  //  AoA
  //  UeRxTxDiff
  //  EnbRxTxDiff
  //  RecvBsr
  //  PuschPrbNum
  //  PdschPrbNum
  //  UlErabSetupPresent
  //  UlPdcpPktLossCtn
  //  UlPdcpPktCtn
  //  DlErabSetupPresent
  //  DlPdcpPktLossCtn
  //  DlPdcpPktCtn
  //  UlPdcpErabTput
  //  DlPdcpErabTput
  //  ErabQCI
  //  RlcUlFlowRate
  //  RlcDlFlowRate
  //  DmacUlFlowRate
  //  DmacDlFlowRate
  //  UlPdcpErabTimerLen
  //  DlPdcpErabTimerLen
  //  AvgRIP
  //  PdschMcsCtn
  //  PuschMcsCtn
  //  PdschAckCtn
  //  PdschNackCtn
  //  PdschDtxCtn
  //  PuschAckCtn
  //  PuschNackCtn
  //  DlRsTxPower
  //  ThermalNoisePower
  //  ServerRsrp
  //  ServerCellEarfcn
  //  ServerCellPci
  //  ServerRsrq
  //  NeighCellNum
  //  NeighCellEarfcn
  //  NeighCellENBID
  //  NeighCellID
  //  NeighPCI
  //  NeighCellRSRP
  //  NeighCellRSRQ
  //  UELat
  //  UELon
  //  IMEI
  //  PdschTmCtn
  //  TA
  //  PdschSingleFlowAveMcs
  //  PdschDoubleFlow1AveMcs
  //  PdschDoubleFlow2AveMcs
  //  PuschAveMcs
  //  Cqi0
  //  Cqi1
  //  SuperCellType
  //  SuperCellCpid
  //  MMEC
  //  MMEApID
  //  MMEGroupID
  //  MRLon
  //  MRLat
  val filterColums: Vector[String] = Vector("MR.LTESCEARFCN",
    "MR.LTESCPCI",
    "MR.LTESCRSRP",
    "MR.LTESCRSRQ",
    "MR.LTESCTADV",
    "MR.LTESCPHR",
    "MR.LTESCAOA",
    "MR.LTESCSINRUL",
    "MR.LTESCRI1",
    "MR.LTESCRI2",
    "MR.LTESCRI4",
    "MR.LTESCRI8",
    "MR.CQI0",
    "MR.CQI1",
    "MR.LATITUDESIGN",
    "MR.LTENCEARFCN",
    "MR.LTENCPCI",
    "MR.LTENCRSRP",
    "MR.LTENCRSRQ",
    "MR.LTESCPUSCHPRBNUM",
    "MR.LTESCPDSCHPRBNUM",
    "MR.LTESCBSR",
    "MR.CDMATYPE",
    "MR.CDMANCBAND",
    "MR.CDMANCARFCN",
    "MR.CDMAPNPHASE",
    "MR.LTECDMAORHRPDNCPILOTSTRENGTH",
    "MR.CDMANCPCI",
    "MR.LONGITUDE",
    "MR.LATITUDE").map(_.toLowerCase)

  val headInfoFromObject = Vector(
    "TimeStamp",
    "MmeUeS1apId",
    "MmeGroupId",
    "MmeCode",
    "id")

  def outputObjectInfo(fileWriter: OutputStreamWriter, line: String): Unit = {
    fileWriter.write(line)
  }
  val eNBIdInXml = "id"
}

trait NorthMR_XML_Reader {
  def parseAndSave(xmlgzFileNames: Vector[String], outFileName: String): Unit = {
    //    接口中不再支持解析 xml 格式文件，只保留gz后的xml文件处理
    val gzs = xmlgzFileNames.filter(_.endsWith(".gz"))
    parseAndSave_gz(gzs, outFileName)
  }
  protected def parseAndSave_gz(xmlgzFileNames: Vector[String], outFileName: String): Unit

}

trait NorthMR_XML_Info_ZTE extends NorthMR_XML_Info {
  override val eNBIdInXml = "MR.eNBId"
  override val headInfoFromObject = Vector(
    "MR.TimeStamp",
    "MR.MmeUeS1apId",
    "MR.MmeGroupId",
    "MR.MmeCode",
    "MR.objectId")
}
