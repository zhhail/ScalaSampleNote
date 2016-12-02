package test

import scala.util.continuations._
import org.scalatest.FlatSpec

/**
 * Created by 10010581 on 2016/11/4.
 */
class Test_test extends FlatSpec {

  var count: (Unit => Unit) = null


  ignore should "reset shift" in {
    reset {
      shift {
        k: (Unit => Unit) =>
          println("kkkkkk")
          count = k
      }
      print("rrrrrrrrrrrrrrrrr")
    }
    print("xxxxxxxxx")
    print(count())
  }
}
