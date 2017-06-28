package com.zte.bigdata.common

trait Using {
  protected def using[A <% {def close() : Unit}, B](param: A)(f: A => B): B =
    try {
      f(param)
    } finally {
      param.close()
    }

}
