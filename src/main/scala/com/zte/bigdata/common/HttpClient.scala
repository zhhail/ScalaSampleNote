package com.zte.bigdata.common

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.ActorSystem
import spray.client.pipelining.sendReceive
import spray.http.{HttpEntity, HttpMethods, HttpRequest, HttpResponse}
import spray.http.ContentTypes.`application/json`


trait HttpClient extends JSONUtil with LogSupport {

  def sendGet(url: String): Future[HttpResponse] = {
    val request = new HttpRequest(uri = spray.http.Uri(url), method = HttpMethods.GET)
    pipeline(request)
  }

  def sendPost(url: String, context: AnyRef): Future[HttpResponse] = {
    val request = new HttpRequest(uri = spray.http.Uri(url),
      method = HttpMethods.POST,
      entity = HttpEntity(`application/json`, toJSON(context)))
    log.debug(s"adapter post ${toJSON(context)} to $url}")
    pipeline(request)
  }

  implicit val actorSystem = ActorSystem("qcell-http")
  val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
}
