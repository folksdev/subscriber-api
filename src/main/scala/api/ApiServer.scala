package api

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import api.db.Db
import api.db.schema.SubscriberTable
import api.subscriber.{SubscriberApi, SubscriberService}

import scala.concurrent.ExecutionContext
import scala.io.StdIn

object ApiServer {
  implicit val actorSystem: ActorSystem[Any] = ActorSystem(Behaviors.empty, "newsletter-api")

  implicit val ec: ExecutionContext = actorSystem.executionContext

  val subscriberTable = new SubscriberTable with Db
  subscriberTable.createIfNotExist
  val subscriberService = new SubscriberService(subscriberTable)
  val subscriberApi = new SubscriberApi(subscriberService)
  val swaggerApi = new SwaggerApi()
  val paths = new Paths(Seq(subscriberApi, swaggerApi))

  def run = {
    val bindFuture = Http().newServerAt("localhost", 8090)
      .bind(paths.routes)

    println(s"Server online at http://localhost:8090/\nPress RETURN to stop...")

    StdIn.readLine()

    bindFuture
      .flatMap(_.unbind())
      .onComplete(_ => actorSystem.terminate())
  }



}
