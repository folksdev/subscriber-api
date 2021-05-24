package api.model

object Period extends Enumeration {
  type Period = Value
  val Weekly = Value(1)
  val BiWeekly = Value(2)
  val Monthly = Value(3)
}
