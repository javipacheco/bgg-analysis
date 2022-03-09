package bgganalysis.services

import bgganalysis.algebras.CVSParser
import bgganalysis.domain.BGGData
import bgganalysis.algebras.AnalyticsConversions
import cats.effect.kernel.Async
import cats.effect.{IO, Sync}
import cats.effect.std.{Console, Queue}
import cats.implicits._
import fs2._
import fs2.io.file._

import scala.concurrent.duration.DurationInt

class BGGAnalyticsService[F[_]: Files: Console: Async](
    cvsParser: CVSParser[F, BGGData],
    analyticsConversions: AnalyticsConversions[F],
    queue: Queue[F, BGGData]
) {

  private val path = Path("data/bgg_dataset.csv")

  def producerDatasetSimulation(): Stream[F, Unit] = {
    for {
      q       <- Stream.eval(Queue.bounded[F, Unit](1))
      bggData <- cvsParser.readCSV(path).chunkAll
      _ <- Stream.awakeEvery[F](1.second).zipRight(Stream.repeatEval(q.offer(()))) concurrently
        Stream
          .repeatEval(q.take)
          .foreach { _ =>
            bggData
              .get(scala.util.Random.nextInt(bggData.size))
              .map(queue.offer)
              .getOrElse(().pure[F])
          }
    } yield ()

  }

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
