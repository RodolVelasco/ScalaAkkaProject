package com.steam.reviews.models

case class Author(steamId: Long, numGamesOwned: Int, numReviews: Int,
                  playtimeForever: Double, playtimeLastTwoWeeks: Double,
                  playtimeAtReview: Double, lastPlayer: Long
                 )