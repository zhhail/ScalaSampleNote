package com.zte.bigdata

import org.apache.hadoop.hive.ql.exec.UDF

class GridSizeConvert extends UDF {
  def evaluate(offset: Integer, toSize: Integer): Integer = {
    // 栅格坐标，不能取到0值。-1和1栅格相邻
    if (offset == null || toSize == null) null
    else if (offset > 0) (offset - 1) / toSize + 1
    else if (offset < 0) (offset + 1) / toSize - 1
    else null
  }
}
