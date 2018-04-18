package uk.co.telegraph.applereceipt

import com.fasterxml.jackson.annotation.{JsonCreator, JsonIgnore, JsonIgnoreProperties}

object Receipt {
  def receipt(receiptData: String) = new Receipt(receiptData)
}

class Receipt @JsonCreator private(val receiptData: String) {
  def getReceiptData: String = receiptData
}