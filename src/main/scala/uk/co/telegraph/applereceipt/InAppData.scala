package uk.co.telegraph.applereceipt

import com.fasterxml.jackson.annotation.{JsonIgnoreProperties, JsonProperty}

@JsonIgnoreProperties(ignoreUnknown = true) class InAppData {
  @JsonProperty("product_id")
  private var productId:String = null

  @JsonProperty("expires_date_ms")
  private var expiresDateMs:String = null

  def getProductId: String = productId

  def setProductId(productId: String): Unit = this.productId = productId

  def getExpiresDateMs: String = expiresDateMs

  def setExpiresDateMs(expiresDateMs: String): Unit = this.expiresDateMs = expiresDateMs

  override def toString = s"InAppData($productId, $expiresDateMs)"
}