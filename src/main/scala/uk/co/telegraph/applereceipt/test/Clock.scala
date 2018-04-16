package uk.co.telegraph.applereceipt.test

trait Clock {
  def now(): Long
}

class SystemClock extends Clock {
  override def now():Long = System.currentTimeMillis()
}