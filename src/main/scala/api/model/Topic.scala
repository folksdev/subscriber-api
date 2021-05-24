package api.model

object Topic extends Enumeration {
  type Topic = Value
  val Scala = Value("scala")
  val Java = Value("java")
  val SpringBoot = Value("spring-boot")
  val GoLang = Value("go-lang")
}
