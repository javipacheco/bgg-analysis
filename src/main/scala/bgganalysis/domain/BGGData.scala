package bgganalysis.domain

case class BGGData(
    id: Long,
    name: String,
    year: Int,
    minPlayers: Int,
    maxPlayers: Int,
    playTime: Int,
    minAge: Int,
    usersRated: Int,
    ratingAverage: Double,
    bggRank: Int,
    complexityAverage: Double
) {
  def canPlayers(players: Int): Boolean =
    players >= minPlayers && players <= maxPlayers
}

