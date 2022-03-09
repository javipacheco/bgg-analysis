package bgganalysis

import bgganalysis.interpreters.{AnalyticsConversionsInterpreter, BGGDataParserInterpreter}
import bgganalysis.services.BGGAnalyticsService
import cats.effect.{IO, IOApp}

object Main extends IOApp.Simple {

  def run: IO[Unit] = {
    new BGGAnalyticsService[IO](
      new BGGDataParserInterpreter[IO],
      new AnalyticsConversionsInterpreter[IO]
    )
      .geekRatingByMinPlayers()
      .compile
      .drain
  }
}
