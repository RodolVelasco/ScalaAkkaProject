package com.steam.reviews.actors

import akka.actor.{ Actor, ActorLogging, ActorRef }
import com.steam.reviews.actors.ReviewPersistenceActor.ReviewProc
import com.steam.reviews.util.JsonLoaderProcessor.{ OnCompleteMessage, OnFailureMessage, OnInitMessage }

object ReviewBulkCoordinator {
  case object OnInitBulkPersistenceMessage
  case object OnCompleteBulkPersistenceMessage
}

class ReviewBulkCoordinator(reviewPersistenceActor: ActorRef) extends Actor with ActorLogging {
  import ReviewBulkCoordinator._
  override def receive: Receive = {
    case OnInitMessage =>
      log.info("OnInitMessage - Bulk initialization")
      reviewPersistenceActor ! OnInitBulkPersistenceMessage
      sender() ! true

    case event @ ReviewProc(review, _, _) =>
      log.debug(s"ReviewProc item to persist: ${review.reviewId}")
      reviewPersistenceActor ! event
      sender() ! "Proceed"

    case OnCompleteMessage =>
      log.info("OnCompleteMessage - Bulk completed")
      reviewPersistenceActor ! OnCompleteBulkPersistenceMessage

    case OnFailureMessage(ex) =>
      log.error("OnFailureMessage - Bulk failure")
      log.error(s"Actor failed with message: ${ex.getMessage}")
  }
}
