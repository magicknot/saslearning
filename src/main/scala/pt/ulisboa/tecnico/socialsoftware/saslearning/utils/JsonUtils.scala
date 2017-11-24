package pt.ulisboa.tecnico.socialsoftware.saslearning.utils

import java.net.URI

import io.circe.{Decoder, Encoder, Json}

object JsonUtils {

  import cats.syntax.either._

  implicit val uriDecoder: Decoder[URI] = Decoder.decodeString.emap { str =>
    Either.catchNonFatal(URI.create(str)).leftMap(t => "Malformed URL.")
  }

  implicit val uriEncoder: Encoder[URI] = Encoder.encodeString.contramap[URI](_.toString)

  // Convert domain errors to JSON
  implicit val encodeException: Encoder[Exception] = Encoder.instance { e =>
    Json.obj(
      "type" -> Json.fromString(e.getClass.getSimpleName),
      "error" -> Json.fromString(Option(e.getMessage).getOrElse("Internal Server Error"))
    )
  }

}
