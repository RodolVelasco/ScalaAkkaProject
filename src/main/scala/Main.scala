import akka.actor.{ ActorRef, ActorSystem, Props }
import akka.http.scaladsl.Http
import com.steam.reviews.actors.ReviewBulkCoordinator
import com.steam.reviews.actors.ReviewPersistenceActor.MostRecentMessages
import com.steam.reviews.api.SteamReviewSystem
import com.steam.reviews.util.JsonLoaderProcessor.{ InitializeBulkByInMemoryProcessor, InitializeBulkByStreamProcessor }
import com.steam.reviews.actors.ReviewPersistenceActor
import com.steam.reviews.validation.{ ReviewValidator }
import com.steam.reviews.util.JsonLoaderProcessor
import com.typesafe.config.ConfigFactory

import scala.language.postfixOps

object Main {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("SteamReviewRootSystem")


    val reviewValidator = system.actorOf(Props[ReviewValidator], "reviewValidator")
    val reviewPersistence = system.actorOf(Props[ReviewPersistenceActor], "reviewPersistence")
    val reviewBulkCoordinator = system.actorOf(Props(new ReviewBulkCoordinator(reviewPersistence)), "reviewBulkCoordinator")

    val jsonLoaderProcessor:ActorRef = system.actorOf(Props(new JsonLoaderProcessor(reviewPersistence)), "JsonProcessor")
    val jsonLoaderProcessorByStream:ActorRef = system.actorOf(Props(new JsonLoaderProcessor(reviewBulkCoordinator)), "JsonProcessorByStream")

    val reviewApiRest = SteamReviewSystem(reviewValidator, reviewPersistence)
    Http().newServerAt("localhost",8080).bind(reviewApiRest.reviewRoute)


    /*
    Process a single/multiline array of json objects from a file into memory,
    converts them into a list of author and reviews objects, and sends them via request
     calls to the API
    */
    //jsonLoaderProcessor ! InitializeBulkByInMemoryProcessor

    /*
    This is a local process that loads a json file that contains multiple review json objects
    that are persisted via akka streams.
     */
    //jsonLoaderProcessorByStream ! InitializeBulkByStreamProcessor
    //reviewPersistence ! MostRecentMessages
  }
}