package com.steam.reviews.api

import akka.actor.{ ActorRef }
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.steam.reviews.actors.ReviewPersistenceActor.ReviewProc
import com.steam.reviews.models.{ ReviewFromJson, ReviewJsonProtocol }
import com.steam.reviews.util.GlobalUtils.reviewFromJsonToReview

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps


object SteamReviewSystemDomain {
  case class ReviewRequest(review: ReviewFromJson, batchNameProcedure: String, batchTotalCount: Int)
  case class ReviewAccepted(review: ReviewFromJson, batchNameProcedure: String, batchTotalCount: Int)
  case object ReviewAcceptedResponse
  case object ReviewRejected
}

case class SteamReviewSystem(reviewValidator: ActorRef, reviewPersistence: ActorRef) extends ReviewJsonProtocol with SprayJsonSupport {
  import SteamReviewSystemDomain._
  implicit val timeout = Timeout(2 seconds)


  val reviewRoute: Route = {
    path("api" / "reviews") {
      post {
        entity(as[ReviewRequest]) { reviewRequest =>
          // TODO decoupled this logic
          val validationResponseFuture = (reviewValidator ? reviewRequest).map {
            case ReviewRejected => StatusCodes.Forbidden
            case ReviewAccepted(reviewFromJson, batchNameProcedure, batchTotalCount) =>
              println(s"Id: ${reviewFromJson.review_id} is about to be sent to persistence layer")
              reviewPersistence ! ReviewProc(reviewFromJsonToReview(reviewFromJson), batchNameProcedure, batchTotalCount)
              StatusCodes.OK
            case _ =>
              println("Bad Request")
              StatusCodes.BadRequest
          }

          complete(validationResponseFuture)
        }
      }
    }
  }

}
