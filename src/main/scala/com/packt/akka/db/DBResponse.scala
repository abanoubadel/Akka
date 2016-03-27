package com.packt.akka.db
import spray.json.DefaultJsonProtocol

case class Created(id: String)

case object Deleted

object Created extends DefaultJsonProtocol {
	println("json proto")
  implicit val UserFormat = jsonFormat1(Created.apply)
}