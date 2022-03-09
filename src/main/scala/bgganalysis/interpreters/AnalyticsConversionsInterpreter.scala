package bgganalysis.interpreters

import bgganalysis.algebras.AnalyticsConversions
import bgganalysis.domain.{BGGAnalysis, BGGData, GeekRatingByPlayers}
import fs2.{Chunk, Pipe, Pull, Stream}

class AnalyticsConversionsInterpreter[F[_]] extends AnalyticsConversions[F] {
  override def toBGGAnalysis: Pipe[F, BGGData, BGGAnalysis] = {
    def go(
        bggAnalysis: BGGAnalysis,
        in: Stream[F, BGGData]
    ): Pull[F, BGGAnalysis, Unit] = in.pull.uncons.flatMap {
      case Some((chunk, tail)) =>
        val accAnalysis: BGGAnalysis =
          chunk.toVector.foldLeft(bggAnalysis)((a, b) => a.accumulate(b))
        Pull.output1(accAnalysis) >> go(accAnalysis, tail)
      case None => Pull.output1(bggAnalysis)
    }
    in => go(BGGAnalysis.empty, in).stream
  }

  override def toGeekRatingByPlayers: Pipe[F, BGGData, GeekRatingByPlayers] = {
    def go(
        geekRatingByPlayers: GeekRatingByPlayers,
        in: Stream[F, BGGData]
    ): Pull[F, GeekRatingByPlayers, Unit] = in.pull.uncons.flatMap {
      case Some((chunk, tail)) =>
        val accRating: GeekRatingByPlayers =
          chunk.toVector.foldLeft(geekRatingByPlayers)((a, b) => a.accumulate(b))
        Pull.output(Chunk(accRating)) >> go(accRating, tail)
      case None => Pull.output1(geekRatingByPlayers)
    }
    in => go(GeekRatingByPlayers.empty, in).stream
  }
}
