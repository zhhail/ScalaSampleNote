package com.zte.bigdata.cleanrobot

import com.zte.bigdata.common.UnitSpec

/**
 * Created by 10010581 on 2016/12/21.
 */

class RobotTest extends UnitSpec{
  describe("temp") {
    it("a") {

      val robot = new Robot
      // 空参数的可以写成如下“流畅”调用
      robot goAhead() goAhead()
      // 无参数的不能写成下面这种“流畅”的调用形式，后一个会被解释成前一个的参数（中缀形式）
      // robot turnRight turnRight
    }
  }
  describe("robot action test") {
    it("should test robot simple action") {
      val robot = new Robot
      robot.execute(GoAhead)
      robot.toString shouldBe "Robot location(0,1), direct: UP"
      robot.execute(TurnLeft)
      robot.toString shouldBe "Robot location(0,1), direct: LEFT"
      robot.execute(GoBack)
      robot.toString shouldBe "Robot location(1,1), direct: LEFT"
      robot.execute(TurnRight)
      robot.toString shouldBe "Robot location(1,1), direct: UP"
    }
    it("should test robot complex action") {
      val robot = new Robot
      val cmd = CmdList(GoAhead, GoAhead, TurnLeft, GoAhead, TurnRight, GoBack)
      robot.execute(cmd).execute(TurnLeft)
      robot.toString shouldBe "Robot location(-1,1), direct: LEFT"
    }
    it("should test robot go 8") {
      val robot = new Robot
      val cmd_round_left = CmdList(GoAhead, TurnLeft, GoAhead, TurnLeft, GoAhead, TurnLeft, GoAhead, TurnLeft)
      val cmd_round_right = CmdList(GoAhead, TurnRight, GoAhead, TurnRight, GoAhead, TurnRight, GoAhead, TurnRight)
      val cmd_go8 = CmdList(cmd_round_left, cmd_round_right)
      robot.execute(cmd_go8)
      robot.toString shouldBe "Robot location(0,0), direct: UP"
    }
  }
}
