package uk.co.telegraph.googleplayreceipt

trait Clock {
  def now(): Long
}

class SystemClock extends Clock {
  override def now():Long = System.currentTimeMillis()
}