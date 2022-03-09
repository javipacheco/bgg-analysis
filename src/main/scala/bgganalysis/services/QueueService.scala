package bgganalysis.services

import bgganalysis.domain.BGGData

import cats.effect._
import cats.effect.std.{Console, Queue}
import fs2._

class QueueService[F[_]: Temporal: Console](
    queue: Queue[F, BGGData]
) {

  def consumer: Stream[F, INothing] =
    Stream
      .repeatEval(queue.take)
      .foreach(n => Console[F].println(s"Consuming '${n.name}' from queue"))

}
