package com.steam.reviews.util

import akka.util.Timeout
import com.steam.reviews.models.{ Author, Review, ReviewFromJson }

import scala.concurrent.duration.{ DurationInt, FiniteDuration }

object ConfigLoader {
  implicit val TIMEOUT: Timeout = 3.seconds
  final val REQUEST_TIME: FiniteDuration = 2.seconds
}
