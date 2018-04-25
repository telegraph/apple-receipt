package uk.co.telegraph.applereceipt

import java.util
import javax.ws.rs.core.HttpHeaders.{ACCEPT, CONTENT_TYPE}
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.Response

import com.typesafe.config.Config
import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime
import org.json4s.JsonAST.{JInt, JNothing}
import org.json4s.native.JsonMethods.{compact, parse, render}
import org.slf4j.LoggerFactory
import uk.co.telegraph.applereceipt.AppleReceiptConstants._
import uk.co.telegraph.applereceipt.HttpStatusCodeMapper.getHttpCodeForItunesResponseCode
import uk.co.telegraph.applereceipt.ResponseGenerator.getErrorCodesForItunesResponse
import uk.co.telegraph.applereceipt.ValidateAppleReceipt.logger
import uk.co.telegraph.applereceipt.model.{ITunesReceipt, ITunesReceiptData, ITunesResponse, InAppData}
import uk.co.telegraph.identity.common.exception.ErrorCode
import uk.co.telegraph.identity.services.api.service.camel.receipt.ResultHolder

import scala.collection.JavaConversions
import scalaj.http.{Http, HttpResponse}

object ValidateAppleReceipt {
  val logger = LoggerFactory.getLogger(classOf[ValidateAppleReceipt])

  private def buildMessage(itunesStatus: ITunesStatus.Status) = itunesStatus.getCode + " " + itunesStatus.getDescription

  def apply(config: Config): ValidateAppleReceipt = new ValidateAppleReceipt(
    config.getString("app.apple.url"),
    config.getString("app.apple.password"),
    config.getString("app.apple.allowedsubscriptions")
  )
}

class ValidateAppleReceipt(val appleUrl:String, val applePassword: String, val allowedSubs: String) {
  private val appleAllowedSubscriptions = allowedSubs.split(",")

  def validate(receiptRequest: Receipt): ResultHolder = {
    val iTunesReceipt:ITunesReceipt = ITunesReceipt(receiptRequest.getReceiptData, applePassword)
    logger.warn("Make request {} to {}", iTunesReceipt.toString, appleUrl)

    val result = callAppleUrl(appleUrl, iTunesReceipt.toString)

    val resultHolder = getITunesResponse(result)
    sendResult(resultHolder)
    resultHolder
  }

  def callAppleUrl(appleUrl:String, iTunesReceipt: String):HttpResponse[String] = {
    Http(appleUrl).postData(iTunesReceipt)
      .header(CONTENT_TYPE, APPLICATION_JSON)
      .header(ACCEPT, APPLICATION_JSON)
      .execute()
  }

  def getITunesResponse(httpResponse : HttpResponse[String]): ResultHolder = {
    val status = parse(httpResponse.body) \ STATUS
    if (httpResponse.isSuccess) {
      val iTunesResponse = new ITunesResponse
      val statusCode = status.asInstanceOf[JInt].num.intValue()
      iTunesResponse.setStatus(statusCode)
      val iTunesReceiptData = new ITunesReceiptData

      val jsonObject = parse(httpResponse.body) \ RECEIPT \ IN_APP

      var inAppDataList = List[InAppData]()

      for (child <- jsonObject.children) {
        val productData = parse(compact(render(child)))
        val inAppData = new InAppData
        if ((productData \ PRODUCT_ID) != JNothing) {
          val productId = productData \ PRODUCT_ID
          inAppData.setProductId(productId.values.toString)
        }
        if ((productData \ EXPIRES_DATE_MS) != JNothing) {
          val expiresData = productData \ EXPIRES_DATE_MS
          inAppData.setExpiresDateMs(expiresData.values.toString)
        }
        inAppDataList ::= inAppData
      }
      iTunesReceiptData.setInAppData(JavaConversions.seqAsJavaList(inAppDataList))
      iTunesResponse.setReceipt(iTunesReceiptData)
      logger.warn("iTunesResponse {}", iTunesResponse)
      getResultHolder(iTunesResponse)
    } else {
      val resultHolder:ResultHolder.Builder = ResultHolder.builder
      resultHolder.response(Response.status(Response.Status.NO_CONTENT).build)
      resultHolder.build
    }
  }

  @throws[Exception]
  private def sendResult(resultHolder: ResultHolder) = if (!resultHolder.response.isEmpty) resultHolder.response.get
  else throw resultHolder.nitroApiException.get

  @throws[java.io.IOException]
  private def getResultHolder(iTunesResponse: ITunesResponse) = {
    val resultHolder:ResultHolder.Builder = ResultHolder.builder
    handleResponse(iTunesResponse, resultHolder)
    resultHolder.build
  }

  private def handleResponse(iTunesResponse: ITunesResponse, resultHolder: ResultHolder.Builder) = {
    if (iTunesResponse.isServerDown)
      failOpen(resultHolder)
    else if (iTunesResponse.isFailed)
      failed(resultHolder, ITunesStatus.getStatus(iTunesResponse.getStatus))
    else
      checkForSubscriptions(resultHolder, iTunesResponse)
  }

  private def checkForSubscriptions(resultHolder: ResultHolder.Builder, iTunesResponse: ITunesResponse): Unit = {
    val currentTime = DateTime.now
    var foundButExpired = false
    import scala.collection.JavaConversions._
    for (appData <- iTunesResponse.getReceipt.getInAppData) {
      if (appleAllowedSubscriptions.contains(appData.getProductId)) {
        foundButExpired = true
        if (StringUtils.isEmpty(appData.getExpiresDateMs) || new DateTime(java.lang.Long.parseLong(appData.getExpiresDateMs)).isAfter(currentTime)) {
          resultHolder.response(Response.status(Response.Status.NO_CONTENT).build)
          return
        }
      }
    }
    if (foundButExpired) failed(resultHolder, ITunesStatus.ERR_SUB_EXPIRED)
    else failed(resultHolder, ITunesStatus.ERR_NOT_AUTH)
  }

  private def failed(resultHolder: ResultHolder.Builder, itunesStatus: ITunesStatus.Status) = {
    var status = Response.Status.INTERNAL_SERVER_ERROR
    var message = "ITunes status missing or unknown."
    var errorCode = ErrorCode.NBE0000
    if (itunesStatus != null) {
      status = getHttpCodeForItunesResponseCode(itunesStatus)
      message = ValidateAppleReceipt.buildMessage(itunesStatus)
      errorCode = getErrorCodesForItunesResponse(itunesStatus)
    }
    ValidateAppleReceipt.logger.warn("Apple receipt validation failed with message {} --- Apple-receipt API responds with http status {} and error code {}", message, status, errorCode)
    resultHolder.response(Response.status(status).build)
  }

  private def failOpen(resultHolder: ResultHolder.Builder) = {
    resultHolder.response(Response.status(Response.Status.NO_CONTENT).build)
  }
}