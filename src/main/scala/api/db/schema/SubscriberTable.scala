package api.db.schema

import api.db.Db
import api.model.Period.Period
import api.model.Topic.Topic
import api.model.{Period, Subscriber, Topic}
import api.model.Subscriber
import slick.jdbc.MySQLProfile.api._
import slick.lifted.Tag

import scala.concurrent.Future


trait SubscriberTable {
  this: Db =>

  implicit val periodMapper = MappedColumnType.base[Period, String](
    e => e.toString,
    s => Period.withName(s)
  )

  implicit val topicMapper = MappedColumnType.base[Set[Topic], String](
    e => e.mkString(";"),
    s => s.split(";").map(Topic.withName).toSet
  )

  class Subscribers(tag: Tag) extends Table[Subscriber](tag, "Subscribers") {
    // Columns
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def email = column[String]("email", O.Length(512))

    def period = column[Period]("period", O.Length(64))

    def topic = column[Set[Topic]]("topic", O.Length(1024))

    // Indexes
    def emailIndex = index("email_idx", email, true)

    // Select
    def * = (id, email, period, topic) <> (Subscriber.tupled, Subscriber.unapply)
  }

  val subscribers = TableQuery[Subscribers]

  def createIfNotExist = db.run(subscribers.schema.createIfNotExists)

  def insert(subscriber: Subscriber): Future[Int] = db.run(subscribers += subscriber)

  def getAll: Future[Seq[Subscriber]] = db.run[Seq[Subscriber]](subscribers.result)

  def getById(id: Long): Future[Option[Subscriber]] = db.run(subscribers.filter(s => s.id === id).result.headOption)

  def update(subscriber: Subscriber): Future[Int]= db.run(subscribers.filter(s => s.id === subscriber.id).update(subscriber))

  def delete(id: Long): Future[Int] = db.run(subscribers.filter(s => s.id === id).delete)

}
