package uk.co.telegraph.applereceipt

import java.util.UUID

import uk.co.telegraph.common.structured.logging.{StructuredLogging, StructuredLoggingRxSchedulerHook}

import scala.collection.immutable.HashMap

object LoggingUtil {
  private val OPERATION_NAME_KEY = "operationName"
  val EVENT_ID_KEY = "eventId"

  private val OPERATION_NAME = "validatereceipt/itunes"
  private val APP_NAME = "apple-receipt"
  


  def initLog(): Unit = {
    new StructuredLoggingRxSchedulerHook
    val customContext : HashMap [String, String] = HashMap ((OPERATION_NAME_KEY , OPERATION_NAME),(EVENT_ID_KEY ,UUID.randomUUID.toString))
    StructuredLogging.init()
    StructuredLogging.initAppName(APP_NAME)
    StructuredLogging.populateMdc(customContext)
  }

}
