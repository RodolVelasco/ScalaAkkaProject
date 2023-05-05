package com.steam.reviews.models

case class ReviewFromJson(field1: Int,
                          app_id: Int, app_name: String, review_id: Long,
                          language: String, review: String,
                          timestamp_created: Long,
                          timestamp_updated: Long,
                          recommended: Boolean, votes_helpful: Int,
                          votes_funny: Long, weighted_vote_score: Double, comment_count: Int,
                          steam_purchase: Boolean, received_for_free: Boolean,
                          written_during_early_access: Boolean, author: AuthorFromJson
                         )