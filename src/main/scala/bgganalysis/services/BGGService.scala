package bgganalysis.services

import bgganalysis.algebras.CVSParser
import bgganalysis.domain.{BGGAnalysis, BGGData, GeekRatingByPlayers}
import cats.effect.Sync
import cats.implicits._
import fs2._
import fs2.io.file._

class BGGService[F[_]: Files: Sync](cvsParser: CVSParser[F, BGGData]) {

  private val path = Path("data/bgg_dataset.csv")

  def showAnalysis(): Stream[F, Unit] = {
    cvsParser
      .readCSV(path)
      .fold(BGGAnalysis.empty) { (acc, item) =>
        acc.accumulate(item)
      }
      .evalMap(a => println(a.show).pure[F])
  }

  def geekRatingByMinPlayers(): Stream[F, Unit] = {
    cvsParser
      .readCSV(path)
      .fold(GeekRatingByPlayers.empty) { (acc, item) =>
        acc.accumulate(item)
      }
      .evalMap(a => println(a.show).pure[F])
  }

}
