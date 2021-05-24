package api

import akka.http.scaladsl.server.Route
import com.github.swagger.akka.SwaggerSite

class Paths(apiPaths: Seq[ApiPath]) extends ApiPath  with SwaggerSite {

  override def routes: Route = concat(apiPaths.map(_.routes): _*) ~ swaggerSiteRoute


}
