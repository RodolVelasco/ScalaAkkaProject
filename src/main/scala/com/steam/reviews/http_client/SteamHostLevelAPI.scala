package com.steam.reviews.http_client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import com.steam.reviews.domain.{ Review, ReviewJsonProtocol }
import spray.json._

import java.util.UUID
import scala.util.{ Failure, Success }

object SteamHostLevelAPI extends App with ReviewJsonProtocol {

  implicit val system = ActorSystem("SteamHostLevelAPI")
  implicit val materializer = ActorMaterializer()
  import system.dispatcher

  val poolFlow = Http().cachedHostConnectionPool[Int]("www.google.com")

  Source(1 to 10)
    .map(i => (HttpRequest(), i))
    .via(poolFlow)
    .map {
      case (Success(response), value) =>
        response.discardEntityBytes()
        s"Request $value has received response: $response"
      case (Failure(ex), value) =>
        s"Request $value has failed: $ex"
    }

  import com.steam.reviews.domain.SteamReviewSystemDomain._

  val reviews = List(
    Review(292030,"The Witcher 3: Wild Hunt",85185111,"schinese","巫师3NB"),
    Review(292030,"The Witcher 3: Wild Hunt",85184605,"english","One of the best RPG's of all time"),
    Review(292030,"The Witcher 3: Wild Hunt",85183602,"turkish","Bla bla bla")
  )

  val reviewRequests = reviews.map(review => ReviewRequest(review, "review-bulk-procedure-hl", reviews.length))
  val serverHttpRequests = reviewRequests.map(reviewRequest =>
    (
      HttpRequest(
        HttpMethods.POST,
        uri = Uri("/api/reviews"),
        entity = HttpEntity(
          ContentTypes.`application/json`,
          reviewRequest.toJson.prettyPrint
        )
      ),
      UUID.randomUUID().toString
    )
  )

  Source(serverHttpRequests)
    .via(Http().cachedHostConnectionPool[String]("localhost",8080))
    .runForeach {
      case (Success(response@HttpResponse(StatusCodes.Forbidden, _, _, _)), responseId) =>
        println(s"Id: $responseId was not allowed to proceed: $response")
      case (Success(response), responseId) =>
        println(s"Id: $responseId was successful and returned the response: $response")
        // do something with the ID: dispatch it, send a notification to the customer, etc
      case (Failure(ex), orderId) =>
        println(s"Id: $orderId could not be completed: $ex")
    }
}
