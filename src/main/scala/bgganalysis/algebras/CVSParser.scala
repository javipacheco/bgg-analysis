package bgganalysis.algebras

import fs2.Stream
import fs2.io.file.Path

trait CVSParser[F[_], A] {

  def readCSV(path: Path): Stream[F, A]

}
