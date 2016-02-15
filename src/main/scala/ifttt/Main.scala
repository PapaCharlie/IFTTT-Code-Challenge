package ifttt

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

/**
  * Main class for IFTTT code challenge
  */
object Main extends App {

  implicit val system = ActorSystem("iftt-one-plus-one")
  val service = system.actorOf(Props[BaseService], "index")
  implicit val timeout = Timeout(5.seconds)
  IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)

}
