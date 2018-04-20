package uk.co.telegraph.applereceipt

import java.util

import com.fasterxml.jackson.annotation.{JsonIgnoreProperties, JsonProperty}

@JsonIgnoreProperties(ignoreUnknown = true)
class ITunesReceiptData {
  @JsonProperty("in_app")
  private var inAppData:util.List[InAppData] = null

  def getInAppData: util.List[InAppData] = inAppData

  def setInAppData(inAppData: util.List[InAppData]): Unit = this.inAppData = inAppData

  override def toString = s"ITunesReceiptData($inAppData)"
}