package com.steam.reviews.validation

import akka.actor.{ Actor, ActorLogging }
import com.steam.reviews.api.SteamReviewSystemDomain
import SteamReviewSystemDomain.{ ReviewAccepted, ReviewRequest }

class ReviewValidator extends Actor with ActorLogging {
  import SteamReviewSystemDomain._

  override def receive: Receive = {

    case ReviewRequest(review, batchNameProcedure, batchTotalCount) =>
      //log.info(s"${review.reviewId} is trying to send this review: ${review.review}")
      log.debug(s"${review.review_id} is validating this review: ${review.review}")
      if(review.language == "n/a") sender() ! ReviewRejected
      else sender() ! ReviewAccepted(review, batchNameProcedure, batchTotalCount) //ReviewAcceptedResponse
  }
}
