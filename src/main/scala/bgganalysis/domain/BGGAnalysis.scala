package bgganalysis.domain

import cats.Show

case class BGGAnalysis(
    oldestGame: Int,
    latestGame: Int,
    can1Players: Int,
    can2Players: Int,
    can3Players: Int,
    can4Players: Int,
    can5orMorePlayers: Int,
    totalGames: Int
) {

  override def toString() =
    s"""
       | Oldest Game: ${oldestGame}
       | Latest Game: ${latestGame}
       | 1 Player: ${can1Players} games
       | 2 Players: ${can2Players} games
       | 3 Players: ${can3Players} games
       | 4 Players: ${can4Players} games
       | 5 or more Players: ${can5orMorePlayers} games
       | Total: ${totalGames} games
       |""".stripMargin

  def accumulate(item: BGGData): BGGAnalysis = {
    copy(
      oldestGame = if (item.year < oldestGame) item.year else oldestGame,
      latestGame = if (item.year > latestGame) item.year else latestGame,
      can1Players = can1Players + (if (item.canPlayers(1)) 1 else 0),
      can2Players = can2Players + (if (item.canPlayers(2)) 1 else 0),
      can3Players = can3Players + (if (item.canPlayers(3)) 1 else 0),
      can4Players = can4Players + (if (item.canPlayers(4)) 1 else 0),
      can5orMorePlayers = can5orMorePlayers + (if (item.maxPlayers >= 5) 1 else 0),
      totalGames = totalGames + 1
    )
  }

}

object BGGAnalysis {

  implicit val showBGGAnalysis: Show[BGGAnalysis] = Show.fromToString

  def empty = BGGAnalysis(Int.MaxValue, 0, 0, 0, 0, 0, 0, 0)
}
