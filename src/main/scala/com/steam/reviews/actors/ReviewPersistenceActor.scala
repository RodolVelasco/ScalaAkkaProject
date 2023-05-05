package com.steam.reviews.actors

import akka.actor.ActorLogging
import akka.persistence._
import com.steam.reviews.actors.ReviewBulkCoordinator.{ OnCompleteBulkPersistenceMessage, OnInitBulkPersistenceMessage }
import com.steam.reviews.actors.ReviewPersistenceActor.{ MostRecentMessages, ReviewProc, ReviewProcRecorded }
import com.steam.reviews.models.Review

import java.util.concurrent.LinkedBlockingQueue


object ReviewPersistenceActor {
  import com.steam.reviews.models.Review
  case class ReviewProc(review: Review, batchNameProcedure: String, batchTotalCount: Int)
  case class ReviewBulk(reviews: List[Review], batchNameProcedure: String, batchTotalCount: Int)
  case class ReviewProcRecorded(review: Review, batchNameProcedure: String, batchCounter: Int, batchTotalCount: Int)
  case class ReviewBulkRecorded(review: Review, batchNameProcedure: String, batchCounter: Int, batchTotalCount: Int)
  case object MostRecentMessages
  case object Shutdown

  // TODO check if this is needs to be removed
  /*case class ReviewsManagerState(
                                reviews: mutable.Map[Int, ActorRef] = mutable.HashMap[Int, ActorRef](),
                                disabledReviews: mutable.Set[Int] = mutable.HashSet[Int]()
                                ) {
    def turnIntoSnapshot: SnapshotContent = SnapshotContent(reviews.keys.toList, disabledReviews)
  }

  case class SnapshotContent(reviews: List[Int], disabledReviews: mutable.Set[Int]) {
    def turnIntoState(implicit context: ActorContext, store: ActorRef): ReviewsManagerState =
      ReviewsManagerState(
        reviews.map(id => id -> context.actorOf(ReviewActor.props(id, store), s"ReviewActor-$id")).to(mutable.HashMap),
        disabledReviews
      )
  }*/

}

case class ReviewPersistenceActor() extends PersistentActor with ActorLogging {

  val REVIEW_SNAPSHOT_BULK_LIMIT = 5000
  var counter = 0
  var counterRecovery = 0
  val lastMessages = new LinkedBlockingQueue[ReviewProcRecorded]()

  override def persistenceId: String = "review-persistence-actor-id"

  override def receiveCommand: Receive = {
    case OnInitBulkPersistenceMessage =>
      log.info("**** OnInitBulkPersistenceMessage ****")
      context.become(bulkDataProcessor)

    case ReviewProc(review, batchNameProcedure, batchTotalCount) =>
      persist(ReviewProcRecorded(review, batchNameProcedure, counter, batchTotalCount))
      { persistedReviewProcRecorded =>
        // TODO check sender() ! Acknowledge in reviewPersistenceActor
        //sender() ! s"Review ${e.review.reviewId} persistenceACK"
        log.debug(s"Persisted ${persistedReviewProcRecorded.review} as review #${persistedReviewProcRecorded.review.reviewId}")
      }
    /*case ReviewBulk(reviews) =>
      persistAll(reviews) { e =>
        log.info(s"Persisted SINGLE $e as review #{${e.reviewId}")
      }*/
    case MostRecentMessages =>
      log.info(s"[MostRecentMessages] Most recent messages: ${lastMessages.size}")
    case SaveSnapshotSuccess(metadata) =>
      log.info(s"[SavingSnapshotSuccess] Saving snapshot succeeded: $metadata")
    //case Shutdown => context.stop(self)
  }

  override def receiveRecover: Receive = {
    case reviewProcRecorded @ ReviewProcRecorded(review, _, _, _) =>
      log.debug(s"[ReceiveRecover] Recovered review id: ${review.reviewId}")
      isSnapshot(reviewProcRecorded)
      counterRecovery += 1
    case SnapshotOffer(metadata, contents) =>
      log.info(s"[ReceiveRecover-SnapshotOffer] Recovered snapshot metadata: $metadata")
      log.info(s"[ReceiveRecover-SnapshotOffer] Recovered snapshot contents: $contents")
    case RecoveryCompleted =>
      log.info("[ReceiveRecover-RecoveryCompleted] Recovery completed")
      log.info(s"[ReceiveRecover-RecoveryCompleted] Recovered reviews total: $counterRecovery")
  }

  def bulkDataProcessor: Receive = {
    case ReviewProc(review, batchNameProcedure, batchTotalCount) =>
      //log.debug(s"Receive review: ${review.reviewId}")
      persist(ReviewProcRecorded(review, batchNameProcedure, counter, batchTotalCount))
      { e =>
        // TODO check sender() ! Acknowledge in reviewPersistenceActor for Bulk by Stream
        //sender() ! s"persistenceACK"
        log.debug(s"Persisted reviews #${e.review.reviewId} as ${e.review}")
        if((counter % REVIEW_SNAPSHOT_BULK_LIMIT) == 0 && counter > 0)
          log.info(s"Counter = $counter")
        counter += 1
        isSnapshot(e)
      }
    case OnCompleteBulkPersistenceMessage =>
      log.info("**** OnCompleteBulkPersistenceMessage ****")
      log.info(s"**** Number of persisted reviews: $counter ****")
      context.unbecome()

    case SaveSnapshotSuccess(metadata) =>
      log.info(s"[SaveSnapshotSuccess] Saving snapshot succeeded: $metadata")
    case SaveSnapshotFailure(metadata, reason) =>
      log.warning(s"[SaveSnapshotFailure] Saving snapshot $metadata failed because of $reason")
  }

  override def onPersistFailure(cause: Throwable, event: Any, seqNr: Long): Unit = {
    log.error(s"[onPersistFailure] Fail to persist $event because of $cause")
    super.onPersistFailure(cause, event, seqNr)
  }

  override def onPersistRejected(cause: Throwable, event: Any, seqNr: Long): Unit = {
    log.error(s"[onPersistRejected] Persis rejected for $event because of $cause")
    super.onPersistRejected(cause, event, seqNr)
  }

  def isSnapshot(reviewProcRecorded: ReviewProcRecorded): Unit = {
    lastMessages.offer(reviewProcRecorded)
    if(lastMessages.size >= REVIEW_SNAPSHOT_BULK_LIMIT) {
      log.debug("[isSnapshot] Saving Snapshot ...")
      saveSnapshot(lastMessages)
      lastMessages.clear()
    }
    if(lastMessages.size > REVIEW_SNAPSHOT_BULK_LIMIT) {
      lastMessages.poll()
    }
  }
}
