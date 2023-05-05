package com.steam.reviews.serialization

import akka.serialization.Serializer
import com.steam.reviews.models.{ Author, Review }
import com.steam.reviews.actors.ReviewPersistenceActor.ReviewProcRecorded
import org.slf4j.LoggerFactory
import spray.json._

class ReviewProcRecordedSerializer extends Serializer with DefaultJsonProtocol {
  implicit val reviewAuthorFormat = jsonFormat7(Author)
  implicit val reviewFormat = jsonFormat16(Review)

  implicit val reviewProcRecordedFormat = jsonFormat4(ReviewProcRecorded.apply)

  implicit object LongCustomJsonFormat extends JsonFormat[Long] {
    def write(obj: Long): JsValue = JsNumber(obj)
    def read(json: JsValue): Long = json match {
      case JsNumber(n) => n.toLong
      case _ => throw new DeserializationException("********** Expected Long value ********** ")
    }
  }

  val log = LoggerFactory.getLogger("reviewProcRecordedSerializer")

  override def identifier: Int = 42351

  override def toBinary(o: AnyRef): Array[Byte] = o match {
    case p: ReviewProcRecorded =>
      //val json: String = p.toJson.prettyPrint
      val json: String = p.toJson.toString()
      log.debug(s"Converting $p")
      log.debug(s"To $json \n")
      json.getBytes()
    case _ => throw new IllegalArgumentException("Only review procedures are supported for this serializer")
  }

  override def fromBinary(bytes: Array[Byte], manifest: Option[Class[_]]): AnyRef = {
    val string = new String(bytes)
    val stringParseJson = string.parseJson
    val reviewProcRecorded = stringParseJson.convertTo[ReviewProcRecorded]
    log.debug(s"Deserialized $stringParseJson")
    log.debug(s"To $reviewProcRecorded \n")
    reviewProcRecorded
  }

  override def includeManifest: Boolean = false
}
