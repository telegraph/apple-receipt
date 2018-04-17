package uk.co.telegraph.applereceipt

import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}

object ITunesReceipt {

  @JsonCreator
  def apply(@JsonProperty("receipt-data") getReceiptData: String, applePassword: String) = new ITunesReceipt(getReceiptData, applePassword)

  def iTunesReceipt(receiptData: String, password: String) = new ITunesReceipt(receiptData, password)
}

class ITunesReceipt private(val receiptData: String, val password: String) {
  def this(@JsonProperty("receipt-data") receiptData: String) {
    this(receiptData, null)
  }

  def getPassword: String = password

  def getReceiptData: String = receiptData

  override def toString: String = Main.OM.writeValueAsString(this)
}