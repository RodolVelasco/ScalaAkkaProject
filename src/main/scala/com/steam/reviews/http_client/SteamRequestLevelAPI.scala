package com.steam.reviews.http_client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ ContentTypes, HttpEntity, HttpMethods, HttpRequest }
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import com.steam.reviews.domain.{ Review, ReviewJsonProtocol }

import scala.util.{ Failure, Success }
import spray.json._

object SteamRequestLevelAPI extends App with ReviewJsonProtocol {

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

  val reviews = List(
    Review(292030,"The Witcher 3: Wild Hunt",85185111,"schinese","巫师3NB"),
    Review(292030,"The Witcher 3: Wild Hunt",85184605,"english","One of the best RPG's of all time"),
    Review(292030,"The Witcher 3: Wild Hunt",85183602,"turkish","Bla bla bla")
  )

  val reviewRequests = reviews.map(review => ReviewRequest(review, "review-bulk-procedure", reviews.length))
  val serverHttpRequests = reviewRequests.map(reviewRequest =>
    HttpRequest(
      HttpMethods.POST,
      uri = "http://localhost:8080/api/reviews",
      entity = HttpEntity(
        ContentTypes.`application/json`,
        reviewRequest.toJson.prettyPrint
      )
    )
  )

  Source(serverHttpRequests)
    .mapAsync(10)(request => Http().singleRequest(request))
    .runForeach(println)
}
