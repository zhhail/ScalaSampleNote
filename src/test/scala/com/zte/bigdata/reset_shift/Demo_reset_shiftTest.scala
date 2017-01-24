package com.zte.bigdata.reset_shift

import com.zte.bigdata.common.UnitSpec


class Demo_reset_shiftTest extends UnitSpec {
  describe("scala continuations") {
    val demo = new Demo_reset_shift
    it("test reset 3 - 带类型的函数") {
      demo.test3
    }
    it("test reset 2 - reset 中 shift之前的语句只会执行一遍") {
      demo.test2
    }
    it("test reset 1 - reset中自带循环") {
      demo.test1
    }
  }
}
