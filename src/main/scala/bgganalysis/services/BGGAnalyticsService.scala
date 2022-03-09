package bgganalysis.services

import bgganalysis.algebras.CVSParser
import bgganalysis.domain.BGGData
import bgganalysis.algebras.AnalyticsConversions
import cats.effect.Sync
import cats.implicits._
import fs2._
import fs2.io.file._

class BGGAnalyticsService[F[_]: Files: Sync](
    cvsParser: CVSParser[F, BGGData],
    analyticsConversions: AnalyticsConversions[F]
) {

  private val path = Path("data/bgg_dataset.csv")

  def showAnalysis(): Stream[F, Unit] = {
    cvsParser
      .readCSV(path)
      .through(analyticsConversions.toBGGAnalysis)
      .evalMap(a => println(a.show).pure[F])
  }

  def geekRatingByMinPlayers(): Stream[F, Unit] = {
    cvsParser
      .readCSV(path)
      .through(analyticsConversions.toGeekRatingByPlayers)
      .evalMap(a => println(a.show).pure[F])
  }

}
