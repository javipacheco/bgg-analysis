import cats.effect._

object Main extends IOApp.Simple {

  def run: IO[Unit] = {
    new BGGService[IO]()
      .geekRatingByMinPlayers()
      .compile
      .drain
  }
}
