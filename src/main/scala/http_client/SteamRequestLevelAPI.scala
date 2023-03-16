package client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.stream.ActorMaterializer

import scala.util.{ Failure, Success }

object SteamRequestLevelAPI extends App {

  implicit val system = ActorSystem("SteamRequestLevelAPI")
  implicit val materializer = ActorMaterializer()
  import system.dispatcher

  val responseFuture = Http().singleRequest(HttpRequest(uri = "http://www.google.com"))

  responseFuture.onComplete {
    case Success(response) =>
      response.discardEntityBytes()
      println(s"Request was successful and returned: $response")
    case Failure(ex) =>
      println(s"Request failed with: $ex")
  }
}
