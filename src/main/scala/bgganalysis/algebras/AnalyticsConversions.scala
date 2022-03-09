package bgganalysis.algebras

import bgganalysis.domain.{BGGAnalysis, BGGData, GeekRatingByPlayers}
import fs2.Pipe

trait AnalyticsConversions[F[_]] {

  def toBGGAnalysis: Pipe[F, BGGData, BGGAnalysis]

  def toGeekRatingByPlayers: Pipe[F, BGGData, GeekRatingByPlayers]

}
