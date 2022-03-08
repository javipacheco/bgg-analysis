package bgganalysis.domain

import cats.Show

case class GeekRatingByPlayers(
    on1Players: List[GeekRatingCount],
    on2Players: List[GeekRatingCount],
    on3Players: List[GeekRatingCount],
    on4Players: List[GeekRatingCount],
    on5Players: List[GeekRatingCount],
    on6Players: List[GeekRatingCount],
    on7Players: List[GeekRatingCount],
    on8Players: List[GeekRatingCount],
    fraction: Double
) {
  def accumulate(item: BGGData): GeekRatingByPlayers = {

    def accCount(list: List[GeekRatingCount], players: Int): List[GeekRatingCount] = {
      if (item.canPlayers(players)) {
        val v: Double =
          BigDecimal(item.ratingAverage - (item.ratingAverage % fraction))
            .setScale(2, BigDecimal.RoundingMode.HALF_UP)
            .toDouble
        list.find(_.value == v).map(i => (list.indexOf(i), i)) match {
          case Some((index, item)) => list.updated(index, item.copy(count = item.count + 1))
          case None                => list :+ GeekRatingCount(v, 1)
        }
      } else list

    }

    copy(
      on1Players = accCount(on1Players, 1),
      on2Players = accCount(on2Players, 2),
      on3Players = accCount(on3Players, 3),
      on4Players = accCount(on4Players, 4),
      on5Players = accCount(on5Players, 5),
      on6Players = accCount(on6Players, 6),
      on7Players = accCount(on7Players, 7),
      on8Players = accCount(on8Players, 8)
    )
  }
}

case class GeekRatingCount(
    value: Double,
    count: Int
)

object GeekRatingByPlayers {

  implicit val showGeekRatingMinPlayers: Show[GeekRatingByPlayers] = Show.show { item =>
    val allRating = item.on1Players ++ item.on2Players ++ item.on3Players ++ item.on4Players ++
      item.on5Players ++ item.on6Players ++ item.on7Players ++ item.on8Players
    val (minorRating, mayorRating, minorCount, mayorCount) =
      allRating.foldLeft((Double.MaxValue, 0d, Double.MaxValue, 0d)) { (acc, item) =>
        (
          if (item.value < acc._1) item.value else acc._1,
          if (item.value > acc._2) item.value else acc._2,
          if (item.count < acc._3) item.count else acc._3,
          if (item.count > acc._4) item.count else acc._4
        )
      }

    val blocks: Int = ((mayorCount - minorCount) / 5).toInt

    def paintValue(list: List[GeekRatingCount], value: Double): String = {
      list.find(_.value == value) match {
        case Some(v) if v.count > mayorCount - blocks       => " * "
        case Some(v) if v.count > mayorCount - (blocks * 2) => " + "
        case Some(v) if v.count > mayorCount - (blocks * 3) => " : "
        case Some(v) if v.count > mayorCount - (blocks * 4) => " - "
        case Some(v) if v.count > mayorCount - (blocks * 5) => " . "
        case _                                              => "   "
      }
    }

    ((BigDecimal(minorRating) to BigDecimal(mayorRating) by item.fraction).reverse.map { value =>
      s"       ${value.toDouble}   ${paintValue(item.on1Players, value.toDouble)} ${paintValue(item.on2Players, value.toDouble)} ${paintValue(
        item.on3Players,
        value.toDouble
      )} ${paintValue(item.on4Players, value.toDouble)} ${paintValue(item.on5Players, value.toDouble)} ${paintValue(
        item.on6Players,
        value.toDouble
      )} ${paintValue(item.on7Players, value.toDouble)} ${paintValue(item.on8Players, value.toDouble)}"
    }.toList ++ List(" Ratings ^    1   2   3   4   5   6   7   8 ", "              > Players"))
      .mkString("\n")
  }

  def empty = GeekRatingByPlayers(
    List.empty,
    List.empty,
    List.empty,
    List.empty,
    List.empty,
    List.empty,
    List.empty,
    List.empty,
    0.5
  )
}
