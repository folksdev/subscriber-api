package api.db

import slick.jdbc.MySQLProfile.api._

trait Db {
   val db = Database.forConfig("appdb")
}
