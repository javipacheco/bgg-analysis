package bgganalysis.interpreters

import fs2._
import bgganalysis.domain.BGGData
import bgganalysis.algebras.CVSParser
import fs2.data.csv.{lowlevel, Row}
import fs2.io.file.{Files, Path}

import scala.util.Try

class BGGDataParserInterpreter[F[_]: Files: RaiseThrowable] extends CVSParser[F, BGGData] {

  override def readCSV(path: Path): Stream[F, BGGData] =
    Files[F]
      .readAll(path)
      .through(csvParser)
      .map(row => parseBGGData(row.values.take(11)))
      .collect { case Some(data) => data }

  private def csvParser: Pipe[F, Byte, Row] =
    _.through(text.utf8.decode)
      .through(lowlevel.rows[F, String](';'))

  private val parseBGGData: List[String] => Option[BGGData] = {
    case (id :: name :: year :: minPlayers :: maxPlayers :: playTime :: minAge :: usersRated :: ratingAverage :: bggRank :: complexityAverage :: Nil) =>
      Try(
        BGGData(
          id = id.toLong,
          name = name,
          year = year.toInt,
          minPlayers = minPlayers.toInt,
          maxPlayers = maxPlayers.toInt,
          playTime = playTime.toInt,
          minAge = minAge.toInt,
          usersRated = usersRated.toInt,
          ratingAverage = ratingAverage.replace(',', '.').toDouble,
          bggRank = bggRank.toInt,
          complexityAverage = complexityAverage.replace(',', '.').toDouble
        )
      ).toOption
    case _ => None
  }

}
