package pt.ulisboa.tecnico.socialsoftware.saslearning

import eu.timepit.refined._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.collection.NonEmpty
import eu.timepit.refined.numeric.NonNegative
import io.circe.{Decoder, Encoder}

package object domain {

  type Natural = Long Refined NonNegative
  type NonEmptyString = String Refined NonEmpty

  implicit val nonEmptyStringEncoder: Encoder[NonEmptyString] = Encoder.encodeString.contramap[NonEmptyString](_.value)
  implicit val nonEmptyStringDecoder: Decoder[NonEmptyString] = Decoder.decodeString.emap { str =>
    refineV[NonEmpty](str)
  }

  def Natural(a: Long): Either[String, Natural] = refineV[NonNegative](a)
  def NonEmptyString(string: String): Either[String, NonEmptyString] = refineV[NonEmpty](string)

  def fromString[T](value: String)(f: NonEmptyString => T): Either[String, T] = {
    refineV[NonEmpty](value).map(str => f(str))
  }


}
