package uk.co.telegraph.applereceipt.model

import com.fasterxml.jackson.annotation.JsonCreator

object Receipt {
  def receipt(receiptData: String) = new Receipt(receiptData)
}

class Receipt @JsonCreator private(val receiptData: String) {
  def getReceiptData: String = receiptData
}