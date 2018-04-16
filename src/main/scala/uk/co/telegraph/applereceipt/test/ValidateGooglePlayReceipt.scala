package uk.co.telegraph.applereceipt.test

import java.nio.charset.Charset
import java.security._
import java.security.spec.{InvalidKeySpecException, X509EncodedKeySpec}
import java.util.Base64

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.SecurityUtils
import com.google.api.services.androidpublisher.{AndroidPublisher, AndroidPublisherScopes}
import com.typesafe.config.Config
import com.typesafe.scalalogging.Logger
import org.apache.commons.lang3.StringUtils

object ValidateGooglePlayReceipt {
  val UNAUTHORIZED = 401
  val FORBIDDEN = 403
  val NO_CONTENT = 204
  val VERIFICATION_FAILED = "verification failed"
  val TOKEN_EXPIRED = "token expired"
  val DATA_SIGNATURE_IS_MISSING = "dataSignature is missing"
  val PURCHASE_DATA_IS_MISSING = "purchaseData is missing"
  private val KEY_FACTORY_ALGORITHM = "RSA"
  private val SIGNATURE_ALGORITHM = "SHA1withRSA"
  val KEY_FILE_NAME = "key.p12"

  val logger: Logger = Logger(classOf[Main])

  @throws[NoSuchAlgorithmException]
  @throws[InvalidKeySpecException]
  private def generatePublicKey(publicKey: String) = {
    val decodedPublicKey = Base64.getDecoder.decode(publicKey)
    val spec = new X509EncodedKeySpec(decodedPublicKey)
    val keyFactory = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM)
    keyFactory.generatePublic(spec)
  }

  def apply(config: Config, clock: Clock = new SystemClock): ValidateGooglePlayReceipt = new ValidateGooglePlayReceipt(
      clock,
      config.getString("app.identity.receipt.google.publicKey"),
      config.getString("app.identity.receipt.google.serviceAccountId"),
      config.getString("app.identity.receipt.google.storePass"),
      config.getString("app.identity.receipt.google.alias"),
      config.getString("app.identity.receipt.google.keyPass")
    )
}

class ValidateGooglePlayReceipt(val clock:Clock, val publicKeys:String, val serviceAccountId:String, val storePass:String, val alias:String, val keyPass:String) {

  def validate(receiptFromRequest: GoogleReceipt): Unit = {
    verifyInputExists(receiptFromRequest)
    verifyInputData(receiptFromRequest.getDataSignature, receiptFromRequest.getPurchaseData)
  }

  private def verifyInputData(dataSignature: String, purchaseData: String): Unit = {
    logger.debug("publicKeys: {}",publicKeys)
    for (publicKeyString <- publicKeys.split(",")) {
      try {
        val publicKey = ValidateGooglePlayReceipt.generatePublicKey(publicKeyString)
        val signature = Signature.getInstance(ValidateGooglePlayReceipt.SIGNATURE_ALGORITHM)
        signature.initVerify(publicKey)
        signature.update(purchaseData.getBytes(Charset.defaultCharset.name))
        if (!signature.verify(Base64.getDecoder.decode(dataSignature.getBytes(Charset.defaultCharset.name))))
          throw new NitroApiException(UNAUTHORIZED, VERIFICATION_FAILED, "NBE1012")
        else if (isPurchaseExpired(purchaseData))
          throw new NitroApiException(ValidateGooglePlayReceipt.FORBIDDEN, ValidateGooglePlayReceipt.TOKEN_EXPIRED, "NBE1023")
      } catch {
        case ne: NitroApiException => throw ne
        case e@(_: InvalidKeyException | _: SignatureException | _: InvalidKeySpecException) =>
          throw new NitroApiException(UNAUTHORIZED, VERIFICATION_FAILED, "NBE1013")
        case e: Exception =>
          throw new NitroApiException(UNAUTHORIZED, VERIFICATION_FAILED, "NBE1014")
      }
    }
  }

  @throws[NitroApiException]
  private def verifyInputExists(receipt: GoogleReceipt):Unit = {
    if (StringUtils.isEmpty(receipt.getDataSignature)) throw new NitroApiException(UNAUTHORIZED, DATA_SIGNATURE_IS_MISSING, "NBE1010")
    if (StringUtils.isEmpty(receipt.getPurchaseData)) throw new NitroApiException(UNAUTHORIZED, PURCHASE_DATA_IS_MISSING, "NBE1011")
  }

  @throws[Exception]
  def isPurchaseExpired(purchaseDataStr: String): Boolean = {
    val purchaseData = stringToGooglePurchaseData(purchaseDataStr)
    val subscriptionPurchase = loadPurchase(storePass, alias, keyPass, serviceAccountId, purchaseData)
    isExpired(subscriptionPurchase.getExpiryTimeMillis)
  }

  @throws[Exception]
  private def loadPurchase(storePass: String, alias: String, keyPass: String, serviceAccountId: String, purchaseData: GooglePurchaseData) = {
    val transport = GoogleNetHttpTransport.newTrustedTransport
    val file = getKey
    if (file.isDefined) {
      val privateKey = SecurityUtils.loadPrivateKeyFromKeyStore(SecurityUtils.getPkcs12KeyStore, file.get, storePass, alias, keyPass)
      val credential = new GoogleCredential.Builder().setTransport(transport).setJsonFactory(JacksonFactory.getDefaultInstance).setServiceAccountId(serviceAccountId).setServiceAccountScopes(AndroidPublisherScopes.all).setServiceAccountPrivateKey(privateKey).build
      val publisher = new AndroidPublisher.Builder(transport, JacksonFactory.getDefaultInstance, credential).build
      val products = publisher.purchases.subscriptions
      val subscription = products.get(purchaseData.getPackageName, purchaseData.getProductId, purchaseData.getPurchaseToken)
      subscription.execute
    }
    else throw new InvalidKeyException("Google api key file is missing!")
  }

  private def getKey = Option.apply(getClass.getClassLoader.getResourceAsStream(KEY_FILE_NAME))

  @throws[Exception]
  protected def stringToGooglePurchaseData(purchaseData: String): GooglePurchaseData = Main.OM.readValue(purchaseData, classOf[GooglePurchaseData])

  protected def isExpired(expiryTime: Long):Boolean = clock.now() > expiryTime
}