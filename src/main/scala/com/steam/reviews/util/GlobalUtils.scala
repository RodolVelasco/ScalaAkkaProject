package com.steam.reviews.util

import com.steam.reviews.models.{ Author, Review, ReviewFromJson }

object GlobalUtils {

  def reviewFromJsonToReview(reviewFromJson: ReviewFromJson): Review = {
    Review(
      reviewFromJson.app_id,
      reviewFromJson.app_name,
      reviewFromJson.review_id,
      reviewFromJson.language,
      reviewFromJson.review,
      reviewFromJson.timestamp_created,
      reviewFromJson.timestamp_updated,
      reviewFromJson.recommended,
      reviewFromJson.votes_helpful,
      reviewFromJson.votes_funny,
      reviewFromJson.weighted_vote_score,
      reviewFromJson.comment_count,
      reviewFromJson.steam_purchase,
      reviewFromJson.received_for_free,
      reviewFromJson.written_during_early_access,
      Author(
        reviewFromJson.author.steamid,
        reviewFromJson.author.num_games_owned,
        reviewFromJson.author.num_reviews,
        reviewFromJson.author.playtime_forever,
        reviewFromJson.author.playtime_last_two_weeks,
        reviewFromJson.author.playtime_at_review,
        reviewFromJson.author.last_played)
    )
  }

}
