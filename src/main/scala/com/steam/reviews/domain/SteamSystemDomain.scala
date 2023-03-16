package com.steam.reviews.domain

import akka.actor.{ Actor, ActorLogging, ActorSystem, Props }
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import spray.json.DefaultJsonProtocol

import scala.concurrent.duration._
import scala.language.postfixOps


case class Review(appId: Int, appName: String, reviewId: Int, language: String, review: String)

object SteamReviewSystemDomain {
  case class ReviewRequest(review: Review, batchNameProcedure: String, batchTotalCount: Int)
  case object ReviewAccepted
  case object ReviewRejected
}

trait ReviewJsonProtocol extends DefaultJsonProtocol {
  implicit val reviewFormat = jsonFormat5(Review)
  implicit val reviewRequestFormat = jsonFormat3(SteamReviewSystemDomain.ReviewRequest)
}

class ReviewValidator extends Actor with ActorLogging {
  import SteamReviewSystemDomain._

  override def receive: Receive = {
    case ReviewRequest(Review(_, appName, reviewId, language, review), batchNameProcedure, batchTotalCount) =>
      log.info(s"$reviewId is trying to send this review: $review")
      if(language == "n/a") sender() ! ReviewRejected
      else sender() ! ReviewAccepted
  }
}

object ReviewSystem extends App with ReviewJsonProtocol with SprayJsonSupport {

  //microservice for reviews
  implicit val system = ActorSystem("ReviewSystem")
  implicit val materializer = ActorMaterializer()
  import system.dispatcher
  import SteamReviewSystemDomain._

  val reviewValidator = system.actorOf(Props[ReviewValidator], "reviewValidator")
  implicit val timeout = Timeout(2 seconds)

  val reviewRoute =
    path("api" / "reviews") {
      post {
        entity(as[ReviewRequest]) { reviewRequest =>
          val validationResponseFuture = (reviewValidator ? reviewRequest).map {
            case ReviewRejected => StatusCodes.Forbidden
            case ReviewAccepted => StatusCodes.OK
            case _ => StatusCodes.BadRequest
          }

          complete(validationResponseFuture)
        }
      }
    }

  Http().bindAndHandle(reviewRoute, "localhost", 8080)
}