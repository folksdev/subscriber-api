package api.model

import api.model.Period.Period
import api.model.Topic.Topic

case class Subscriber(id: Long, email: String, period: Period, topic: Set[Topic])

