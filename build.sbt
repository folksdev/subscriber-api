organization := "com.github.folksdev"
name := "newsletter-api"


version := "0.1"

scalaVersion := "2.13.5"


lazy val `newsletter-api` = project
  .in(file("."))
  .configs(IntegrationTest)
  .settings(
    libraryDependencies ++= dependency.deps,
    Defaults.itSettings,
    IntegrationTest / fork := true
  )



lazy val versions = new {
  val Akka = "2.6.8"
  val AkkaHttp = "10.2.3"
  val Swagger = "2.1.7"
  val JavaxWs = "2.0.1"
  val ScalaTest = "3.2.0"
  val typesafeConfig = "1.4.1"
}

lazy val dependency = new {

  lazy val deps = akka ++ swagger ++ javaxWs ++ slick ++ typesafeConfig ++ testDep

  lazy val testDep = Seq(
    "org.scalatest" %% "scalatest" % versions.ScalaTest % "it, test",
    "com.typesafe.akka" %% "akka-stream-testkit" % versions.Akka % "test",
    "com.typesafe.akka" %% "akka-http-testkit" % versions.AkkaHttp % "test",
    "org.mockito" %% "mockito-scala-scalatest" % "1.14.4" % "test",
    "com.dimafeng" %% "testcontainers-scala-scalatest" % "0.39.0" % "it"

  )

  lazy val akka = Seq(
    "com.typesafe.akka" %% "akka-actor-typed" % versions.Akka,
    "com.typesafe.akka" %% "akka-http" % versions.AkkaHttp,
    "com.typesafe.akka" %% "akka-stream" % versions.Akka,
    "com.typesafe.akka" %% "akka-http-spray-json" % versions.AkkaHttp
  )

  lazy val swagger = Seq(
    "com.github.swagger-akka-http" %% "swagger-akka-http" % "2.4.0",
    "com.github.swagger-akka-http" %% "swagger-scala-module" % "2.3.0",
    "com.github.swagger-akka-http" %% "swagger-enumeratum-module" % "2.1.0",
    "io.swagger.core.v3" % "swagger-core" % versions.Swagger,
    "io.swagger.core.v3" % "swagger-annotations" % versions.Swagger,
    "io.swagger.core.v3" % "swagger-models" % versions.Swagger,
    "io.swagger.core.v3" % "swagger-jaxrs2" % versions.Swagger,
    "co.pragmati" %% "swagger-ui-akka-http" % "1.3.0"
  )

  lazy val javaxWs = Seq(
    "javax.ws.rs" % "javax.ws.rs-api" % versions.JavaxWs
  )

  lazy val slick = Seq(
    "com.typesafe.slick" %% "slick" % "3.3.3",
    "mysql" % "mysql-connector-java" % "8.0.23"
  )

  lazy val typesafeConfig = Seq(
    "com.typesafe" % "config" % versions.typesafeConfig
  )
}
