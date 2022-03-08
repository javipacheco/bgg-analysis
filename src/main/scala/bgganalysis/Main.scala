package bgganalysis

import bgganalysis.interpreters.BGGDataParserInterpreter
import bgganalysis.services.BGGService
import cats.effect.{IO, IOApp}

object Main extends IOApp.Simple {

  def run: IO[Unit] = {
    new BGGService[IO](new BGGDataParserInterpreter[IO])
      .geekRatingByMinPlayers()
      .compile
      .drain
  }
}
