package uk.co.telegraph.applereceipt

import java.util
import javax.ws.rs.POST
import javax.ws.rs.core.Response
import javax.ws.rs.core.HttpHeaders.{ACCEPT, CONTENT_TYPE}
import javax.ws.rs.core.MediaType.APPLICATION_JSON

import scalaj.http.Http
import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import uk.co.telegraph.identity.common.exception.ErrorCode
import uk.co.telegraph.identity.services.api.service.camel.receipt.ResultHolder
import uk.co.telegraph.applereceipt.HttpStatusCodeMapper.getHttpCodeForItunesResponseCode
import uk.co.telegraph.applereceipt.ResponseGenerator.getErrorCodesForItunesResponse
import com.typesafe.config.Config

object ValidateAppleReceipt {
  val APPLE_RECEIPT_VALIDATOR_ROUTE_ID = "CS-appleValidateReceipt"
  val APPLE_RECEIPT_VALIDATOR_ENDPOINT: String = "direct:" + APPLE_RECEIPT_VALIDATOR_ROUTE_ID
  private val CALL_APPLE_SERVICE_ROUTE_ID = "CallAppleService"
  private val logger = LoggerFactory.getLogger(classOf[ValidateAppleReceipt])

  private def buildMessage(itunesStatus: ITunesStatus.Status) = itunesStatus.getCode + " " + itunesStatus.getDescription

  def apply(config: Config): ValidateAppleReceipt = new ValidateAppleReceipt(
    config.getString("app.apple.url"),
    config.getString("app.apple.password"),
    config.getString("app.apple.allowedsubscriptions")
  )
}

class ValidateAppleReceipt(val appleUrl:String, val applePassword: String, val allowedSubs: String) {
  private var appleAllowedSubscriptions = util.Arrays.asList(allowedSubs.split(","))

  def validate(receiptRequest: ITunesReceipt): Unit = {
    val result = Http("http4://receiptValidationHost?httpClientConfigurer=appleHttpClientConfigurer").postData(receiptRequest.toString)
      .header(CONTENT_TYPE, APPLICATION_JSON)
      .header(ACCEPT, APPLICATION_JSON)
      .asString
  }

//  @throws[Exception]
//  private def sendResult(resultHolder: ResultHolder) = if (resultHolder.response.isPresent) exchange.getOut.setBody(resultHolder.response.get)
//  else throw resultHolder.nitroApiException.get

//  @throws[java.io.IOException]
//  private def getResultHolder(responseCode: Integer) = {
//    val resultHolder:ResultHolder.Builder = ResultHolder.builder
//    if (Response.Status.OK.getStatusCode != responseCode) failOpen(resultHolder)
//    else handleResponse(resultHolder)
//    resultHolder.build
//  }
//
//  private def handleResponse(resultHolder: ResultHolder.Builder) = {
//    val iTunesResponse:ITunesResponse = exchange.getIn.getBody(classOf[ITunesResponse])
//    if (iTunesResponse.isServerDown) failOpen(resultHolder)
//    else if (iTunesResponse.isFailed) failed(resultHolder, ITunesStatus.getStatus(iTunesResponse.getStatus))
//    else checkForSubscriptions(resultHolder, iTunesResponse)
//  }

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
    ValidateAppleReceipt.logger.warn("Apple receipt validation failed with message {} --- Identity responds with http status {} and error code {}", message, status, errorCode)
    resultHolder.response(Response.status(status).build)
  }

  private def failOpen(resultHolder: ResultHolder.Builder) = resultHolder.response(Response.status(Response.Status.NO_CONTENT).build)
}