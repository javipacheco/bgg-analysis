package bgganalysis

import bgganalysis.interpreters.{AnalyticsConversionsInterpreter, BGGDataParserInterpreter}
import bgganalysis.services.{BGGAnalyticsService, QueueService}
import bgganalysis.domain.BGGData
import cats.effect.std.Queue
import cats.effect.{IO, IOApp}
import fs2.Stream

object Main extends IOApp.Simple {

  def run: IO[Unit] = {
    (for {
      queue <- Stream.eval(Queue.bounded[IO, BGGData](10))
      queueService = new QueueService[IO](queue)
      bggAnalyticsService = new BGGAnalyticsService[IO](
        new BGGDataParserInterpreter[IO],
        new AnalyticsConversionsInterpreter[IO],
        queue
      )
      _ <- bggAnalyticsService.producerDatasetSimulation() concurrently queueService.consumer
    } yield ()).compile.drain
  }
}
