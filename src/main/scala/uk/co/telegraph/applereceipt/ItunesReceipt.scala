package uk.co.telegraph.applereceipt

import com.fasterxml.jackson.annotation.JsonProperty

object ItunesReceipt {
  def iTunesReceipt(receiptData: String, password: String) = new ItunesReceipt(receiptData, password)
}

class ItunesReceipt private(val receiptData: String, val password: String) {
  def this(@JsonProperty("receipt-data") receiptData: String) {
    this(receiptData, null)
  }

  def getPassword: String = password

  def getReceiptData: String = receiptData
}