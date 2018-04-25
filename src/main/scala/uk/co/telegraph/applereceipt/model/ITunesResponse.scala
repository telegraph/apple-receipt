package uk.co.telegraph.applereceipt.model

import java.util

import com.fasterxml.jackson.annotation.{JsonIgnore, JsonIgnoreProperties, JsonProperty}
import uk.co.telegraph.applereceipt.ITunesStatus

@JsonIgnoreProperties(ignoreUnknown = true) class ITunesResponse {

  @JsonProperty("status")
  private var status:Int = 0
  private var exception:String = null

  @JsonProperty("receipt")
  private var receipt:ITunesReceiptData = null

  @JsonIgnore
  def validateProductId(allowedSubscriptionArray: util.List[String]): Boolean = allowedSubscriptionArray.contains(receipt.getInAppData.get(0).getProductId)

  def getStatus: Int = status

  def setStatus(status: Int): Unit = this.status = status

  def getException: String = exception

  def setException(exception: String): Unit = this.exception = exception

  def getReceipt: ITunesReceiptData = receipt

  def setReceipt(receipt: ITunesReceiptData): Unit = this.receipt = receipt

  def isServerDown: Boolean = ITunesStatus.ERR_SERVER_DOWN.getCode == status

  def isFailed: Boolean = ITunesStatus.OK.getCode != status

  override def toString: String = "ITunesResponse{" + "status=" + status + ", receipt=" + receipt + '}'
}