package com.steam.reviews.models

case class Review(appId: Int, appName: String, reviewId: Long,
                  language: String, review: String,
                  timestampCreated: Long,
                  timestampUpdated: Long,
                  recommended: Boolean, votesHelpful: Int,
                  votesFunny: Long, weightedVoteScore: Double, commentCount: Int,
                  steamPurchase: Boolean, receivedForFree: Boolean,
                  writtenDuringEarlyAccess: Boolean, author: Author
                 )
