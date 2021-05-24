package api.subscriber

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import api.ApiPath
import api.model.Subscriber
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.{Content, Schema}
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.{Operation, Parameter}
import javax.ws.rs._
import javax.ws.rs.core.MediaType

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Path("/subscriber")
class SubscriberApi(subscriberService: SubscriberService)(implicit val ec: ExecutionContext) extends ApiPath {

  val routes: Route = getAll ~ getById ~ create ~ updateById ~ deleteSubscriber

  @GET
  @Produces(Array(MediaType.APPLICATION_JSON))
  @Operation(summary = "Return all subscribers",
    responses = Array(
      new ApiResponse(responseCode = "200", description = "Successful response",
        content = Array(new Content(schema = new Schema(implementation = classOf[List[Subscriber]])))),
      new ApiResponse(responseCode = "500", description = "Internal server error"))
  )
  def getAll: Route =
    path("subscriber") {
      get {
        pathEnd {
          val subscribersFuture: Future[List[Subscriber]] = subscriberService.getAll.map(_.toList)

          onComplete(subscribersFuture) {
            case Success(subscribers) => complete(subscribers)
            case Failure(exception) => complete(StatusCodes.InternalServerError, exception.getMessage)
          }
        }
      }
    }

  @GET
  @Path("/{id}")
  @Produces(Array(MediaType.APPLICATION_JSON))
  @Operation(summary = "Return subscriber by id",
    parameters = Array(
      new Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "subs id")
    ),
    responses = Array(
      new ApiResponse(responseCode = "200", description = "Successful response",
        content = Array(new Content(schema = new Schema(implementation = classOf[List[Subscriber]])))),
      new ApiResponse(responseCode = "404", description = "User not found"),
      new ApiResponse(responseCode = "500", description = "Internal server error"))
  )
  def getById: Route =
    pathPrefix("subscriber" / LongNumber) { subscriberId =>
      get {
        pathEnd {
          val subscribersFuture: Future[Option[Subscriber]] = subscriberService.getById(subscriberId)

          onComplete(subscribersFuture) {
            case Success(maybeSubscriber) => maybeSubscriber match {
              case Some(subscriber) => complete(subscriber)
              case None => complete(StatusCodes.NotFound)
            }

            case Failure(exception) => complete(StatusCodes.InternalServerError, exception.getMessage)
          }
        }
      }
    }

  @POST
  @Produces(Array(MediaType.APPLICATION_JSON))
  @Operation(summary = "Create new subscriber",
    requestBody = new RequestBody(content = Array(new Content(schema = new Schema(implementation = classOf[Subscriber])))),
    responses = Array(
      new ApiResponse(responseCode = "201", description = "Successful response",
        content = Array(new Content(schema = new Schema(implementation = classOf[Subscriber])))),
      new ApiResponse(responseCode = "400", description = "Bad Request"),
      new ApiResponse(responseCode = "500", description = "Internal server error"))
  )
  def create: Route =
    path("subscriber") {
      post {
        pathEnd {
          entity(as[Subscriber]) { subscriber =>
            val subscribersFuture: Future[Subscriber] = subscriberService.insert(subscriber)
              .map(i => subscriber.copy(id = i))

            onComplete(subscribersFuture) {
              case Success(_) => complete(StatusCodes.Created)
              case Failure(exception) => complete(StatusCodes.InternalServerError, exception.getMessage)

            }
          }
        }
      }
    }

  @PUT
  @Path("/{id}")
  @Produces(Array(MediaType.APPLICATION_JSON))
  @Operation(summary = "Update subscriber by id",
    requestBody = new RequestBody(content = Array(new Content(schema = new Schema(implementation = classOf[Subscriber])))),
    parameters = Array(
      new Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "subs id")
    ),
    responses = Array(
      new ApiResponse(responseCode = "202", description = "Successful response",
        content = Array(new Content(schema = new Schema(implementation = classOf[Subscriber])))),
      new ApiResponse(responseCode = "400", description = "Bad Request"),
      new ApiResponse(responseCode = "404", description = "Subscriber not found"),
      new ApiResponse(responseCode = "500", description = "Internal server error"))
  )
  def updateById: Route =
    pathPrefix("subscriber" / LongNumber) { subscriberId =>
      put {
        pathEnd {
          entity(as[Subscriber]) { subscriber =>
            val subscribersFuture: Future[Int] = subscriberService.update(subscriber)

            onComplete(subscribersFuture) {
              case Success(_) => complete(StatusCodes.Accepted)
              case Failure(exception) =>
                exception match {
                  case e: IllegalArgumentException => complete(StatusCodes.BadRequest, e.getMessage)
                  case e: Throwable => complete(StatusCodes.InternalServerError, e.getMessage)
                }

            }
          }
        }
      }
    }

  @DELETE
  @Path("/{id}")
  @Operation(summary = "Delete Subscriber",
    parameters = Array(
      new Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "subs id")
    ),
    responses = Array(
      new ApiResponse(responseCode = "202", description = "Accepted"),
      new ApiResponse(responseCode = "404", description = "Subscriber Not Found"),
      new ApiResponse(responseCode = "500", description = "Internal server error")
    )
  )
  def deleteSubscriber: Route =
    pathPrefix("subscriber" / LongNumber) { subscriberId =>
      delete {
        pathEnd {
          val future = subscriberService.delete(subscriberId)
          onComplete(future) {
            case Success(_) => complete(StatusCodes.OK)
            case Failure(exception) => complete(StatusCodes.InternalServerError, exception.getMessage)
          }
        }
      }
    }
}

