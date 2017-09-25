package com.zte.bigdata.common


class TestTest extends UnitSpec {
  describe("String Util split"){
    it("test split"){
      strSplit("ab,cd,ef") shouldBe List("ab", "cd", "ef")
      strSplit("ab,,ef") shouldBe List("ab", "", "ef")
      strSplit("ab,,ef,,") shouldBe List("ab", "", "ef", "", "")
      strSplit( """ab,"cd,ef",""") shouldBe List("ab", "\"cd,ef\"", "")
      strSplit( """ab,"cd,ef",gg,,hh""") should be(List("ab","\"cd,ef\"","gg","","hh"))

    }
  }
}
