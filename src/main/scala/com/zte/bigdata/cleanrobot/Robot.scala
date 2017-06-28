package com.zte.bigdata.cleanrobot

sealed abstract class Command

case object GoAhead extends Command

case object GoBack extends Command

case object TurnLeft extends Command

case object TurnRight extends Command

case class CmdList(ls: Command*) extends Command

class Robot {
  def log(msg: String) = {
    //    println(f"$msg%-10s -- new status: $toString")
    //    println(msg)
  }

  override def toString = s"Robot location($x,$y), direct: $direction"

  def turnLeft: this.type = {
    direction match {
      case LEFT => direction = DOWN
      case DOWN => direction = RIGHT
      case RIGHT => direction = UP
      case UP => direction = LEFT
    }
    log("turn left")
    this
  }

  def turnRight: this.type = {
    direction match {
      case LEFT => direction = UP
      case DOWN => direction = LEFT
      case RIGHT => direction = DOWN
      case UP => direction = RIGHT
    }
    log("turn right")
    this
  }

  def goAhead(): this.type = {
    direction match {
      case LEFT => x -= 1
      case DOWN => y -= 1
      case RIGHT => x += 1
      case UP => y += 1
    }
    log(s"go ahead")
    this
  }

  def goBack: this.type = {
    direction match {
      case LEFT => x += 1
      case DOWN => y += 1
      case RIGHT => x -= 1
      case UP => y -= 1
    }
    log(s"go back")
    this
  }

  def execute(cmd: Command): this.type = {
    cmd match {
      case cmdls: CmdList => cmdls.ls.foreach(execute)
      case GoAhead => goAhead
      case GoBack => goBack
      case TurnLeft => turnLeft
      case TurnRight => turnRight
    }
    this
  }

  sealed abstract class DIRECTION

  case object UP extends DIRECTION

  case object LEFT extends DIRECTION

  case object DOWN extends DIRECTION

  case object RIGHT extends DIRECTION

  private var x = 0
  private var y = 0
  private var direction: DIRECTION = UP

}
