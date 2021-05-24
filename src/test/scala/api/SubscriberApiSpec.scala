package api

import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.MessageEntity
import akka.http.scaladsl.testkit.ScalatestRouteTest
import api.model.{ModelJsonSupport, Period, Subscriber, Topic}
import api.subscriber.{SubscriberApi, SubscriberService}
import org.mockito.scalatest.MockitoSugar
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future

class SubscriberApiSpec extends AnyFlatSpec
  with Matchers
  with ScalatestRouteTest
  with ModelJsonSupport
  with ScalaFutures
  with MockitoSugar {


  val subscriberService = mock[SubscriberService]
  val subsApi = new SubscriberApi(subscriberService)

  it should "get all subscribers" in {
    // setup
    val subsList = Seq(
      Subscriber(1, "a@a.com", Period.Weekly, Set(Topic.GoLang, Topic.Scala)),
      Subscriber(2, "b@b.com", Period.BiWeekly, Set(Topic.Java, Topic.Scala)),

    )


    when(subscriberService.getAll).thenReturn(Future(subsList))

    // call
    Get("/subscriber") ~> subsApi.getAll ~> check {

      //verify
      responseAs[List[Subscriber]] should contain theSameElementsAs subsList
    }
  }

  it should "return 500 if exception happen" in {
    // setup
    when(subscriberService.getAll).thenThrow(new RuntimeException())

    // call
    Get("/subscriber") ~> subsApi.getAll ~> check {

      //verify
      response.status.intValue() shouldBe 500
    }
  }

  it should "get subscriber by id" in {

    val sub = Subscriber(1, "a@a.com", Period.Weekly, Set(Topic.GoLang, Topic.Scala))
    when(subscriberService.getById(1)).thenReturn(Future(Some(sub)))

    Get("/subscriber/1") ~> subsApi.getById ~> check {
      responseAs[Subscriber] shouldBe sub
    }
  }

  it should "return 404 if user with id is not found" in {

    when(subscriberService.getById(1)).thenReturn(Future(None))

    Get("/subscriber/1") ~> subsApi.getById ~> check {
      response.status.intValue() shouldBe 404
    }
  }

  it should "create new subscriber" in {
    val subscriber = Subscriber(1, "a@a.com", Period.Weekly, Set(Topic.GoLang, Topic.Scala))
    when(subscriberService.insert(subscriber)).thenReturn(Future(1))

    val subs2 = subscriber.copy(id = 2)
    val subscriberEntity = Marshal(subscriber).to[MessageEntity].futureValue

    Post("/subscriber").withEntity(subscriberEntity) ~> subsApi.create ~> check {
      response.status.intValue() shouldBe 201
    }
  }

  it should "return 500 if exception happens" in {

    reset(subscriberService)

    val subscriber = Subscriber(1, "a@a.com", Period.Weekly, Set(Topic.GoLang, Topic.Scala))
    val expectedSub = Subscriber(1, "a@a.com", Period.BiWeekly, Set(Topic.Java))
    when(subscriberService.insert(any[Subscriber])).thenThrow(new RuntimeException())

    val subscriberEntity = Marshal(subscriber).to[MessageEntity].futureValue

    Post("/subscriber").withEntity(subscriberEntity) ~> subsApi.create ~> check {
      response.status.intValue() shouldBe 500
      verify(subscriberService, times(1)).insert(subscriber)
    }
  }

}
