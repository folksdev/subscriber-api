package api.model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import api.model.Period.Period
import api.model.Topic.Topic
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, RootJsonFormat}

trait ModelJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {

  implicit def enumFormat[T <: Enumeration](implicit enu: T): RootJsonFormat[T#Value] =
    new RootJsonFormat[T#Value] {
      def write(obj: T#Value): JsValue = JsString(obj.toString)

      def read(json: JsValue): T#Value = {
        json match {
          case JsString(txt) => enu.withName(txt)
          case somethingElse => throw DeserializationException(s"Expected a value from enum $enu instead of $somethingElse")
        }
      }
    }


  implicit val periodFormat: RootJsonFormat[Period] = enumFormat(Period)
  implicit val topicFormat: RootJsonFormat[Topic] = enumFormat(Topic)
  implicit val subscriberFormat = jsonFormat4(Subscriber)
}
