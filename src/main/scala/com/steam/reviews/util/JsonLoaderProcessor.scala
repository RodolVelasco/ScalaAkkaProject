package com.steam.reviews.util

import akka.NotUsed
import akka.actor.{ Actor, ActorLogging, ActorRef, ActorSystem, Props }
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.{ ActorAttributes, Supervision, ThrottleMode }
import akka.stream.alpakka.json.scaladsl.JsonReader
import akka.stream.scaladsl.{ FileIO, Flow, Sink, Source }
import akka.util.ByteString
import com.steam.reviews.models.{ ReviewFromJson, ReviewJsonProtocol }
import com.steam.reviews.actors.ReviewPersistenceActor.ReviewProc
import com.steam.reviews.util.JsonLoaderProcessor.{ InitializeBulkByInMemoryProcessor, InitializeBulkByStreamProcessor, OnCompleteMessage, OnFailureMessage, OnInitMessage }
import com.steam.reviews.util.GlobalUtils.reviewFromJsonToReview
import com.typesafe.config.ConfigFactory
import spray.json._

import java.util.UUID
import scala.util.{ Failure, Success }
import java.io.File
import java.nio.file.Paths
import java.util.concurrent.atomic.AtomicReference
import scala.concurrent.duration.DurationInt

object JsonLoaderProcessor {
  def props(reviewPersistenceActor: ActorRef): Props = Props(new JsonLoaderProcessor(reviewPersistenceActor))

  case object OnInitMessage
  case object OnCompleteMessage
  case class OnFailureMessage(exception: Throwable)

  case object InitializeBulkByInMemoryProcessor
  case object InitializeBulkByStreamProcessor

}

class JsonLoaderProcessor(reviewPersistenceActor: ActorRef) extends Actor with ReviewJsonProtocol with ActorLogging {
  implicit val system:ActorSystem = context.system
  val lastProcessedByteString = new AtomicReference[Option[ByteString]](None)

  override def receive: Receive = {
    case InitializeBulkByInMemoryProcessor => {

      println("InitializeSingleLineInMemoryProcessor")

      //val config = ConfigFactory.load().getConfig("myCustomConfiguration")
      //val dataInputFlag = config.getString("dataInput")

      //val dataInputFlag = "json"

      import com.steam.reviews.api.SteamReviewSystemDomain._

      /*@JsonCodec case class Author(@JsonKey("steamid") steamId: Long,
                                   @JsonKey("num_games_owned") numGamesOwned: Int,
                                   @JsonKey("num_reviews") numReviews: Int)

      @JsonCodec case class Review(@JsonKey("app_id") appId: Int,
                                   @JsonKey("app_name") appName: String,
                                   @JsonKey("review_id") reviewId: Int,
                                   @JsonKey("language") language: String,
                                   @JsonKey("review") review: String,
                                   @JsonKey("author") author: Author)*/

      /*val reviews = dataInputFlag match {
        case "list" =>
          List(
            Review(292030,"The Witcher 3: Wild Hunt",85185111,"schinese","巫师3NB", Author(76561199095369542L,6,2)),
            Review(292030,"The Witcher 3: Wild Hunt",85184605,"english","One of the best RPG's of all time",Author(76561198949504115L,30,10)),
            Review(292030,"The Witcher 3: Wild Hunt",85183602,"turkish","Bla bla bla",Author(76561199090098988L,5,1))
          )
        case "empty" =>
          List()
        case "json" =>

        //case "csv" => ???
        case _ =>
          List()
      }*/
      /**/

      /*val reviewsJsonStringAllFields =
        """
          |[{"field1":"0","app_id":"292030","app_name":"The Witcher 3: Wild Hunt","review_id":"85185598","language":"schinese","review":"不玩此生遗憾，RPG游戏里的天花板，太吸引人了","timestamp_created":"1611381629","timestamp_updated":"1611381629","recommended":"True","votes_helpful":"0","votes_funny":"0","weighted_vote_score":"0.0","comment_count":"0","steam_purchase":"True","received_for_free":"False","written_during_early_access":"False","author":{"steamid":"76561199095369542","num_games_owned":"6","num_reviews":"2","playtime_forever":"1909.0","playtime_last_two_weeks":"1448.0","playtime_at_review":"1909.0","last_played":"1611343383.0"}},{"field1":"1","app_id":"292030","app_name":"The Witcher 3: Wild Hunt","review_id":"85185250","language":"schinese","review":"拔DIAO无情打桩机--杰洛特!!!","timestamp_created":"1611381030","timestamp_updated":"1611381030","recommended":"True","votes_helpful":"0","votes_funny":"0","weighted_vote_score":"0.0","comment_count":"0","steam_purchase":"True","received_for_free":"False","written_during_early_access":"False","author":{"steamid":"76561198949504115","num_games_owned":"30","num_reviews":"10","playtime_forever":"2764.0","playtime_last_two_weeks":"2743.0","playtime_at_review":"2674.0","last_played":"1611386307.0"}},{"field1":"2","app_id":"292030","app_name":"The Witcher 3: Wild Hunt","review_id":"85185111","language":"schinese","review":"巫师3NB","timestamp_created":"1611380800","timestamp_updated":"1611380800","recommended":"True","votes_helpful":"0","votes_funny":"0","weighted_vote_score":"0.0","comment_count":"0","steam_purchase":"True","received_for_free":"False","written_during_early_access":"False","author":{"steamid":"76561199090098988","num_games_owned":"5","num_reviews":"1","playtime_forever":"1061.0","playtime_last_two_weeks":"1061.0","playtime_at_review":"1060.0","last_played":"1611383777.0"}},{"field1":"3","app_id":"292030","app_name":"The Witcher 3: Wild Hunt","review_id":"85184605","language":"english","review":"One of the best RPG's of all time, worthy of any collection","timestamp_created":"1611379970","timestamp_updated":"1611379970","recommended":"True","votes_helpful":"0","votes_funny":"0","weighted_vote_score":"0.0","comment_count":"0","steam_purchase":"True","received_for_free":"False","written_during_early_access":"False","author":{"steamid":"76561199054755373","num_games_owned":"5","num_reviews":"3","playtime_forever":"5587.0","playtime_last_two_weeks":"3200.0","playtime_at_review":"5524.0","last_played":"1611383744.0"}},{"field1":"4","app_id":"292030","app_name":"The Witcher 3: Wild Hunt","review_id":"85184287","language":"schinese","review":"大作","timestamp_created":"1611379427","timestamp_updated":"1611379427","recommended":"True","votes_helpful":"0","votes_funny":"0","weighted_vote_score":"0.0","comment_count":"0","steam_purchase":"True","received_for_free":"False","written_during_early_access":"False","author":{"steamid":"76561199028326951","num_games_owned":"7","num_reviews":"4","playtime_forever":"217.0","playtime_last_two_weeks":"42.0","playtime_at_review":"217.0","last_played":"1610788249.0"}},{"field1":"5","app_id":"292030","app_name":"The Witcher 3: Wild Hunt","review_id":"85184171","language":"english","review":"good story, good graphics. lots to do.","timestamp_created":"1611379264","timestamp_updated":"1611379264","recommended":"True","votes_helpful":"0","votes_funny":"0","weighted_vote_score":"0.0","comment_count":"0","steam_purchase":"True","received_for_free":"False","written_during_early_access":"False","author":{"steamid":"76561198170193529","num_games_owned":"11","num_reviews":"1","playtime_forever":"823.0","playtime_last_two_weeks":"823.0","playtime_at_review":"823.0","last_played":"1611379201.0"}},{"field1":"6","app_id":"292030","app_name":"The Witcher 3: Wild Hunt","review_id":"85184064","language":"english","review":"dis gud,","timestamp_created":"1611379091","timestamp_updated":"1611379091","recommended":"True","votes_helpful":"0","votes_funny":"0","weighted_vote_score":"0.0","comment_count":"0","steam_purchase":"True","received_for_free":"False","written_during_early_access":"False","author":{"steamid":"76561198119302812","num_games_owned":"27","num_reviews":"2","playtime_forever":"4192.0","playtime_last_two_weeks":"3398.0","playtime_at_review":"4192.0","last_played":"1611351734.0"}},{"field1":"7","app_id":"292030","app_name":"The Witcher 3: Wild Hunt","review_id":"85183602","language":"turkish","review":".","timestamp_created":"1611378312","timestamp_updated":"1611378312","recommended":"True","votes_helpful":"0","votes_funny":"0","weighted_vote_score":"0.0","comment_count":"0","steam_purchase":"True","received_for_free":"False","written_during_early_access":"False","author":{"steamid":"76561199084188849","num_games_owned":"9","num_reviews":"1","playtime_forever":"2701.0","playtime_last_two_weeks":"0.0","playtime_at_review":"2701.0","last_played":"1609671218.0"}}]
          |""".stripMargin*/

      /*val reviewsJsonStringSomeFields =
        """
          |[{"app_id": "292030", "app_name": "The Witcher 3: Wild Hunt", "review_id": "85185598", "language": "schinese", "review": "不玩此生遗憾，RPG游戏里的天花板，太吸引人了", "author": {"steamid": "76561199095369542", "num_games_owned": "6", "num_reviews": "2"}}, {"app_id": "292030", "app_name": "The Witcher 3: Wild Hunt", "review_id": "85185250", "language": "schinese", "review": "拔DIAO无情打桩机--杰洛特!!!", "author": {"steamid": "76561198949504115", "num_games_owned": "30", "num_reviews": "10"}}, {"app_id": "292030", "app_name": "The Witcher 3: Wild Hunt", "review_id": "85185111", "language": "schinese", "review": "巫师3NB", "author": {"steamid": "76561199090098988", "num_games_owned": "5", "num_reviews": "1"}}, {"app_id": "292030", "app_name": "The Witcher 3: Wild Hunt", "review_id": "85184605", "language": "english", "review": "One of the best RPG's of all time, worthy of any collection", "author": {"steamid": "76561199054755373", "num_games_owned": "5", "num_reviews": "3"}}, {"app_id": "292030", "app_name": "The Witcher 3: Wild Hunt", "review_id": "85184287", "language": "schinese", "review": "大作", "author": {"steamid": "76561199028326951", "num_games_owned": "7", "num_reviews": "4"}}, {"app_id": "292030", "app_name": "The Witcher 3: Wild Hunt", "review_id": "85184171", "language": "english", "review": "good story, good graphics. lots to do.", "author": {"steamid": "76561198170193529", "num_games_owned": "11", "num_reviews": "1"}}, {"app_id": "292030", "app_name": "The Witcher 3: Wild Hunt", "review_id": "85184064", "language": "english", "review": "dis gud,", "author": {"steamid": "76561198119302812", "num_games_owned": "27", "num_reviews": "2"}}, {"app_id": "292030", "app_name": "The Witcher 3: Wild Hunt", "review_id": "85183602", "language": "turkish", "review": ".", "author": {"steamid": "76561199084188849", "num_games_owned": "9", "num_reviews": "1"}}]
          |""".stripMargin*/

      // TODO: bring this from properties
      //val filePath = "src/main/resources/xaa_500mb.json"
      val filePath = "src/main/resources/xaa_8lines.json"
      //val filePath = "src/main/resources/xaa_100000lines.json"
      //val filePath = "src/main/resources/xaa_1000lines_linebreaks.json"
      val file = new File(filePath)
      val reviewsString = scala.io.Source.fromFile(file).getLines.mkString
      val reviewsJson = reviewsString.parseJson
      val reviews = reviewsJson.convertTo[List[ReviewFromJson]]

      val batchNameProcedure = "review-by-request-bulk-procedure"
      val batchTotalCount = reviews.length

      /*val reviewList = List(
        Review(0,292030,"The Witcher 3: Wild Hunt",85185598,"schinese","不玩此生遗憾，RPG游戏里的天花板，太吸引人了",1611381629,1611381629,true,0,0,0.0,0,true,false,false,Author(76561199095369540L,6,2,1909.0,1448.0,1909.0,1611343383))
        //Review(292030,"The Witcher 3: Wild Hunt",85185111,"schinese","巫师3NB", Author(76561199095369542L,6,2)),
        //Review(292030,"The Witcher 3: Wild Hunt",85184605,"english","One of the best RPG's of all time",Author(76561198949504115L,30,10)),
        //Review(292030,"The Witcher 3: Wild Hunt",85183602,"turkish","Bla bla bla",Author(76561199090098988L,5,1))
      )
      println(reviewList)*/

      val reviewRequests = reviews.map(review => ReviewRequest(review, batchNameProcedure, batchTotalCount))
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

    case InitializeBulkByStreamProcessor => {
      log.info("InitializeBulkByStreamProcessor")

      // incremental testing load
      //val filePath = "src/main/resources/xaa_1000lines.json"
      //val filePath = "src/main/resources/xaa_25000lines.json"
      val filePath = "src/main/resources/xaa_100000lines.json" //56,638 OK

      // Ultimo elemento con review_id 27980849
      // 1,640,231 review total -> ultima vez que realice query obtuve 1,017,166
      //val filePath = "src/main/resources/xaa_500mb.json"

      //val filePath = "src/main/resources/problematic_8.json"
      //val filePath = "src/main/resources/problematic_58.json"
      //val filePath = "src/main/resources/problematic_13.json" //some more jsons after 500mb

      // 3,092,544 review total -> ultimo elemento con review_id 75431361
      //val filePath = "src/main/resources/1gb/xaa_1gb.json"
      //some more json after 1gb
      //val filePath = "src/main/resources/problematic_after1gb_100.json"

      val path = Paths.get(filePath)
      val src = FileIO.fromPath(path)
      val batchNameProcedure = "review-by-stream-bulk-procedure"
      val batchTotalCount = 0
      //Json must be between brackets [ {}, {},...]
      val selectJson: Flow[ByteString, ByteString, NotUsed] = JsonReader.select("$[*]")
      val parseJson: Flow[ByteString,  ReviewProc, NotUsed] = Flow.fromFunction { str: ByteString =>
        try {
          val value = str.utf8String.parseJson.convertTo[ReviewFromJson]
          val reviewProc = ReviewProc(reviewFromJsonToReview(value), batchNameProcedure, batchTotalCount)
            reviewProc
        } catch {
          case e: Exception =>
            log.error(s"Error encountered: ${e.getMessage} for ByteString: $str")
            throw e
        }
      }

      src
        .via(selectJson)
        .via(parseJson)
        .withAttributes(ActorAttributes.supervisionStrategy {
          case e: Exception =>
            log.error(s"********** Error found: ${e.getMessage} **********")
            Supervision.Resume
        })
        .throttle(10000, 20.seconds, 100000, ThrottleMode.Shaping)
        .runWith(Sink.actorRefWithBackpressure(reviewPersistenceActor, OnInitMessage, OnCompleteMessage, t => OnFailureMessage(t)))
    }
  }

}
