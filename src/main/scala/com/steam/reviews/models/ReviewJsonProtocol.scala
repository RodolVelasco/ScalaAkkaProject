package com.steam.reviews.models

import com.steam.reviews.api.SteamReviewSystemDomain
import spray.json.DefaultJsonProtocol

trait ReviewJsonProtocol extends DefaultJsonProtocol  {

  // TODO borrar comentarios
  /*implicit object BooleanCustomJsonFormat extends JsonFormat[Boolean] {
    def write(value: Boolean): JsValue = JsBoolean(value)
    def read(json: JsValue): Boolean = json match {
      case JsBoolean(b) => b
      //case JsString(s) if s.equalsIgnoreCase("true") => true
      case _ => deserializationError("Expected Boolean value")
    }
  }*/

  // TODO borrar comentarios
  /*implicit object FloatCustomJsonFormat extends JsonFormat[Float] {
    def write(obj: Float): JsValue = JsNumber(obj)
    def read(json: JsValue): Float = json match {
      case JsNumber(n) => n.floatValue
      case JsString(n) => n.toFloat.floatValue
      case _ => throw new DeserializationException("Expected Float as JsNumber")
    }
  }

  implicit object RowIdJsonFormat extends JsonFormat[Int] {
    def write(obj: Int): JsValue = JsNumber(obj)
    //def write(obj: String): JsValue = JsNumber(obj.toInt)
    def read(json: JsValue): Int = json match {
      case JsNumber(n) =>
      n.intValue
      case JsString(s) => s.toInt
      case _ => throw new DeserializationException("Expected Int as JsNumber or JsString")
    }
  }*/

  // TODO borrar comentarios
  /*implicit object StringCustomJsonFormat extends JsonFormat[String] {
    def write(value: String): JsValue = JsString(value)
    def read(json: JsValue): String = json match {
      case JsString(s) => s
      case JsBoolean(b) => b.toString
      case _ => throw deserializationError("Expected String value")
    }
  }*/

  // TODO borrar comentarios
  /*implicit object DateTimeFormat extends RootJsonFormat[DateTime] {
    def write(obj: DateTime): JsValue = JsNumber(obj.clicks)
    def read(json: JsValue): DateTime = json match {
      case JsNumber(n) => DateTime(n.toLong * 1000)
      case JsString(n) => DateTime(n.toLong * 1000)
      case _ => throw new DeserializationException("Expected DateTime as a number")
    }
  }*/

  implicit val reviewAuthorFormat = jsonFormat7(AuthorFromJson.apply)
  implicit val reviewFormat = jsonFormat17(ReviewFromJson.apply)
  implicit val reviewRequestFormat = jsonFormat3(SteamReviewSystemDomain.ReviewRequest)
}