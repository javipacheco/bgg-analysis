import cats.effect.Sync
import fs2.data.csv._
import fs2.io.file._
import fs2._
import cats.implicits._
import scala.util.Try

class BGGService[F[_]: Files: Sync] {

  private val path = Path("data/bgg_dataset.csv")

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

  private def readCSV(): Stream[F, BGGData] = {
    Files[F]
      .readAll(path)
      .through(csvParser)
      .map(row => parseBGGData(row.values.take(11)))
      .collect { case Some(data) => data }
  }

  def showAnalysis(): Stream[F, Unit] = {
    readCSV()
      .fold(BGGAnalysis.empty) { (acc, item) =>
        acc.accumulate(item)
      }
      .evalMap(a => println(a.show).pure[F])
  }

  def geekRatingByMinPlayers(): Stream[F, Unit] = {
    readCSV()
      .fold(GeekRatingByPlayers.empty) { (acc, item) =>
        acc.accumulate(item)
      }
      .evalMap(a => println(a.show).pure[F])
  }

}
