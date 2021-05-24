package api

import akka.http.scaladsl.server.{Directives, Route}
import akka.util.Timeout
import api.model.ModelJsonSupport
import scala.concurrent.duration._

trait ApiPath extends Directives with ModelJsonSupport {

  implicit val timeout: Timeout = Timeout(2.seconds)

  def routes: Route

}
