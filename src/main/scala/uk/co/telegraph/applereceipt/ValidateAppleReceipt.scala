package uk.co.telegraph.applereceipt

import java.util
import javax.ws.rs.core.HttpHeaders.{ACCEPT, CONTENT_TYPE}
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.Response

import com.typesafe.config.Config
import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import uk.co.telegraph.applereceipt.HttpStatusCodeMapper.getHttpCodeForItunesResponseCode
import uk.co.telegraph.applereceipt.ResponseGenerator.getErrorCodesForItunesResponse
import uk.co.telegraph.applereceipt.ValidateAppleReceipt.logger
import uk.co.telegraph.identity.common.exception.ErrorCode
import uk.co.telegraph.identity.services.api.service.camel.receipt.ResultHolder

import scala.util.parsing.json.JSON
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
  private var appleAllowedSubscriptions = util.Arrays.asList(allowedSubs.split(","))

  def validate(receiptRequest: Receipt): Unit = {
    val iTunesReceipt:ITunesReceipt = ITunesReceipt(receiptRequest.getReceiptData, applePassword)
    logger.warn("request {}", iTunesReceipt.toString)

    val result:HttpResponse[String] = Http(appleUrl).postData(iTunesReceipt.toString)
      .header(CONTENT_TYPE, APPLICATION_JSON)
      .header(ACCEPT, APPLICATION_JSON)
      .execute()

    val jsonObject = JSON.parseFull(result.body)
    val fields = jsonObject.get.asInstanceOf[Map[String, Any]]
    val statusCode = fields.get("status").get.asInstanceOf[Double].toInt
    logger.warn("statusCode from ITunes {}", statusCode)

    val status = ITunesStatus.getStatus(statusCode)

    logger.warn("Status {}", status)
    logger.warn("Description {}", status.description)
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