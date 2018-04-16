package uk.co.telegraph.applereceipt

import java.util

import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime
import org.slf4j.LoggerFactory

object AppleReceiptValidatorRoute {
  val APPLE_RECEIPT_VALIDATOR_ROUTE_ID = "CS-appleValidateReceipt"
  val APPLE_RECEIPT_VALIDATOR_ENDPOINT: String = "direct:" + APPLE_RECEIPT_VALIDATOR_ROUTE_ID
  private[apple] val CALL_APPLE_SERVICE_ROUTE_ID = "CallAppleService"
  private val logger = LoggerFactory.getLogger(classOf[AppleReceiptValidatorRoute])

  private[apple] def buildMessage(itunesStatus: ITunesStatus.type) = itunesStatus.getCode + " " + itunesStatus.getDescription
}

class AppleReceiptValidatorRoute extends Nothing {
  private var appleUrl = null
  private val appleWaitTimeout = null
  private var applePassword = null
  private var appleAllowedSubscriptions = null

  @throws[Exception]
  def configure(): Unit = {
    appleUrl = getContext.resolvePropertyPlaceholders("{{apple.url}}")
    //appleWaitTimeout = Integer.valueOf(getContext().resolvePropertyPlaceholders("{{apple.wait.timeout}}"));
    applePassword = getContext.resolvePropertyPlaceholders("{{apple.password}}")
    appleAllowedSubscriptions = util.Arrays.asList(getContext.resolvePropertyPlaceholders("{{apple.allowedsubscriptions}}").split(","))
    from(AppleReceiptValidatorRoute.APPLE_RECEIPT_VALIDATOR_ENDPOINT).streamCaching.removeHeaders("CamelHttp*").process(new Nothing() {
      @throws[Exception]
      def process(exchange: Nothing): Unit = {
        val receiptFromRequest = exchange.getIn.getBody(classOf[Receipt])
        val receipt = iTunesReceipt(receiptFromRequest.getReceiptData, applePassword)
        exchange.getIn.setBody(receipt)
      }
    }).marshal.json(JsonLibrary.Jackson).to("{{apple.endpoint}}").unmarshal.json(JsonLibrary.Jackson, classOf[ITunesResponse]).process(new Nothing() {
      @throws[Exception]
      def process(exchange: Nothing): Unit = {
        val resultHolder = getResultHolder(exchange)
        sendResult(exchange, resultHolder)
      }
    }).end.routeId(AppleReceiptValidatorRoute.APPLE_RECEIPT_VALIDATOR_ROUTE_ID)
    from("{{apple.endpoint}}").setHeader(CONTENT_TYPE, constant(APPLICATION_JSON)).setHeader(ACCEPT, constant(APPLICATION_JSON)).setHeader(HTTP_METHOD, constant(POST)).setHeader(HTTP_URI, simple(appleUrl)).to("http4://receiptValidationHost?httpClientConfigurer=appleHttpClientConfigurer").end.routeId(AppleReceiptValidatorRoute.CALL_APPLE_SERVICE_ROUTE_ID)
  }

  @throws[Exception]
  private def sendResult(exchange: Nothing, resultHolder: Nothing) = if (resultHolder.response.isPresent) exchange.getOut.setBody(resultHolder.response.get)
  else throw resultHolder.nitroApiException.get

  @throws[java.io.IOException]
  private def getResultHolder(exchange: Nothing) = {
    val resultHolder = ResultHolder.builder
    if (OK.getStatusCode ne exchange.getIn.getHeader(Exchange.HTTP_RESPONSE_CODE, classOf[Integer])) failOpen(resultHolder)
    else handleResponse(exchange, resultHolder)
    resultHolder.build
  }

  private def handleResponse(exchange: Nothing, resultHolder: Nothing) = {
    val iTunesResponse = exchange.getIn.getBody(classOf[ITunesResponse])
    if (iTunesResponse.isServerDown) failOpen(resultHolder)
    else if (iTunesResponse.isFailed) failed(resultHolder, ITunesStatus.getStatus(iTunesResponse.getStatus))
    else checkForSubscriptions(resultHolder, iTunesResponse)
  }

  private def checkForSubscriptions(resultHolder: Nothing, iTunesResponse: ITunesResponse): Unit = {
    val currentTime = DateTime.now
    var foundButExpired = false
    import scala.collection.JavaConversions._
    for (appData <- iTunesResponse.getReceipt.getInAppData) {
      if (appleAllowedSubscriptions.contains(appData.getProductId)) {
        foundButExpired = true
        if (StringUtils.isEmpty(appData.getExpiresDateMs) || new Nothing(Long.parseLong(appData.getExpiresDateMs)).isAfter(currentTime)) {
          resultHolder.response(Response.status(Response.Status.NO_CONTENT).build)
          return
        }
      }
    }
    if (foundButExpired) failed(resultHolder, ITunesStatus.ERR_SUB_EXPIRED)
    else failed(resultHolder, ITunesStatus.ERR_NOT_AUTH)
  }

  private def failed(resultHolder: Nothing, itunesStatus: ITunesStatus.type) = {
    var status = Response.Status.INTERNAL_SERVER_ERROR
    var message = "ITunes status missing or unknown."
    var errorCode = ErrorCode.NBE0000
    if (itunesStatus != null) {
      status = getHttpCodeForItunesResponseCode(itunesStatus)
      message = AppleReceiptValidatorRoute.buildMessage(itunesStatus)
      errorCode = getErrorCodesForItunesResponse(itunesStatus)
    }
    AppleReceiptValidatorRoute.logger.warn("Apple receipt validation failed with message {} --- Identity responds with http status {} and error code {}", message, status, errorCode)
    resultHolder.response(Response.status(status).build)
  }

  private def failOpen(resultHolder: Nothing) = resultHolder.response(Response.status(Response.Status.NO_CONTENT).build)
}