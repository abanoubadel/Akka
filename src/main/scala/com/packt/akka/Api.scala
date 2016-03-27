package com.packt.akka

import spray.json._
import DefaultJsonProtocol._
import akka.actor.ActorSystem
import scala.concurrent.Future
import akka.stream.{ ActorMaterializer, Materializer }
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import com.packt.akka.models._
import akka.http.scaladsl.model.StatusCodes._
import com.packt.akka.db.TweetManager
import com.packt.akka.db.UserManager
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import scala.concurrent.ExecutionContext

trait RestApi {
  // import TweetProtocol._
  // import TweetEntity._
  import TweetEntityProtocol.EntityFormat3

  import UserProtocol._
  import UserEntity._
  import UserEntityProtocol.EntityFormat

  implicit val system: ActorSystem

  implicit val materializer: Materializer

  implicit val ec: ExecutionContext

  ///////////////////////////////////////////////////////
  import spray.json.DefaultJsonProtocol
  case class Created(id: String)
  case object Deleted
  object Created extends DefaultJsonProtocol {
    println("json proto")
    implicit val UserFormat = jsonFormat1(Created.apply)
  }
  ///////////////////////////////////////////////////////

  val route =
    pathPrefix("users"){
      pathPrefix("login"){
        (post & entity(as[User])) { user =>
          complete {
            UserManager.login(user) map { r =>
              OK -> Created(r.get.id.stringify)
            }
          }
        }
      } ~ 
      pathPrefix("signup") {
        (post & entity(as[User])) { user =>
          complete {
            println("req")
            UserManager.save(user).toJson
            
          }
        }
      }
    } 
}
object Api extends App with RestApi {

  override implicit val system = ActorSystem("Akka-Project")

  override implicit val materializer = ActorMaterializer()

  override implicit val ec = system.dispatcher
 
  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
 
  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  Console.readLine()
 
  bindingFuture
    .flatMap(_.unbind()) 
    .onComplete(_ => system.shutdown())
}