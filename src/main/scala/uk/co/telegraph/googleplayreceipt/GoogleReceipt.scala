package uk.co.telegraph.googleplayreceipt

import com.fasterxml.jackson.annotation.{JsonCreator, JsonIgnoreProperties, JsonProperty}

object GoogleReceipt {
  @JsonCreator
  def apply(@JsonProperty("purchaseData") purchaseData: String = null, @JsonProperty("dataSignature") dataSignature: String = null) = new GoogleReceipt(purchaseData, dataSignature)
}

@JsonIgnoreProperties(ignoreUnknown = true)
class GoogleReceipt(val purchaseData: String,
                    val dataSignature: String) {

  @JsonProperty("purchaseData")
  def getPurchaseData: String = {
    purchaseData
  }

  @JsonProperty("dataSignature")
  def getDataSignature: String = {
    dataSignature
  }

  override def toString: String = Main.OM.writeValueAsString(this)

}