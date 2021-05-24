package api

import java.io.File

import spray.json._
import DefaultJsonProtocol._
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest}
import api.model.{ModelJsonSupport, Period, Subscriber, Topic}
import com.dimafeng.testcontainers.{DockerComposeContainer, ForAllTestContainer}
import org.scalatest.BeforeAndAfter
import org.scalatest.concurrent.PatienceConfiguration.{Interval, Timeout}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

class ApiIntegrationSpec extends AnyFlatSpec with Matchers with ForAllTestContainer with BeforeAndAfter with ModelJsonSupport with ScalaFutures {
  override def container: DockerComposeContainer = DockerComposeContainer(new File("integration/docker-compose.yml"))

  implicit val actorSystem: ActorSystem[Any] = ActorSystem(Behaviors.empty, "int-test")
  implicit val ec: ExecutionContext = actorSystem.executionContext


  it should "be able to do crud" in {
    ApiServer.run

    val sub = Subscriber(0, "a@a.com", Period.Weekly, Set(Topic.GoLang, Topic.Scala))

    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = "http://localhost:8090/subscriber",
      entity = HttpEntity(ContentTypes.`application/json`, sub.toJson.toString())
    )

    val responseFuture = Http().singleRequest(request)

    whenReady(responseFuture, Timeout(1.minutes), Interval(20.seconds)) { response =>
      println(response.entity)

      response.status.intValue() shouldBe 201

    }

  }
}
