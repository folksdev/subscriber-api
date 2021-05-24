package api.subscriber

import api.db.schema.SubscriberTable
import api.model.Subscriber

import scala.concurrent.Future

class SubscriberService(subscriberTable: SubscriberTable) {

  def getAll: Future[Seq[Subscriber]]= {
    subscriberTable.getAll
  }

  def insert(subscriber: Subscriber): Future[Int] = subscriberTable.insert(subscriber)

  def getById(id: Long): Future[Option[Subscriber]] = subscriberTable.getById(id)

  def update(subscriber: Subscriber): Future[Int] = subscriberTable.update(subscriber)

  def delete(id: Long): Future[Int] = subscriberTable.delete(id)
}
