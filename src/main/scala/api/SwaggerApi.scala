package api

import api.subscriber.SubscriberApi
import com.github.swagger.akka.SwaggerHttpService
import com.github.swagger.akka.model.Info
import io.swagger.v3.oas.models.ExternalDocumentation

class SwaggerApi extends ApiPath with SwaggerHttpService{

  override val apiClasses = Set(classOf[SubscriberApi])
  //override val host = "localhost:8080"
  override val info: Info = Info(version = "1.0")
  override val externalDocs: Option[ExternalDocumentation] = Some(new ExternalDocumentation().description("Core Docs").url("http://acme.com/docs"))
  //override val securitySchemeDefinitions = Map("basicAuth" -> new BasicAuthDefinition())
  //override val unwantedDefinitions = Seq("Function1", "Function1RequestContextFutureRouteResult")
}
