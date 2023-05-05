package com.steam.reviews.models

case class AuthorFromJson(steamid: Long, num_games_owned: Int, num_reviews: Int,
                          playtime_forever: Double, playtime_last_two_weeks: Double,
                          playtime_at_review: Double, last_played: Long
                         )