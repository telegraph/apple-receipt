package uk.co.telegraph.googleplayreceipt

import com.fasterxml.jackson.annotation.{JsonCreator, JsonIgnoreProperties}


object GooglePurchaseData {
  @JsonCreator
  def apply(): GooglePurchaseData = new GooglePurchaseData()
}

@JsonIgnoreProperties(ignoreUnknown = true)
class GooglePurchaseData() {
  private var orderId:String = null
  private var packageName:String = null
  private var productId:String = null
  private var purchaseState:String = null
  private var developerPayload:String = null
  private var purchaseToken:String = null
  private var autoRenewing:String = null
  private var purchaseTime:Long = 0L

  def getOrderId: String = orderId

  def setOrderId(orderId: String): Unit = this.orderId = orderId

  def getPackageName: String = packageName

  def setPackageName(packageName: String): Unit = this.packageName = packageName

  def getProductId: String = productId

  def setProductId(productId: String): Unit = this.productId = productId

  def getPurchaseState: String = purchaseState

  def setPurchaseState(purchaseState: String): Unit = this.purchaseState = purchaseState

  def getDeveloperPayload: String = developerPayload

  def setDeveloperPayload(developerPayload: String): Unit = this.developerPayload = developerPayload

  def getPurchaseToken: String = purchaseToken

  def setPurchaseToken(purchaseToken: String): Unit = this.purchaseToken = purchaseToken

  def getAutoRenewing: String = autoRenewing

  def setAutoRenewing(autoRenewing: String): Unit = this.autoRenewing = autoRenewing

  def getPurchaseTime: Long = purchaseTime

  def setPurchaseTime(purchaseTime: Long): Unit = this.purchaseTime = purchaseTime
}

