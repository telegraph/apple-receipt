package uk.co.telegraph.applereceipt


import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status._

import com.typesafe.config.{Config, ConfigFactory}
import org.hamcrest.CoreMatchers._
import org.joda.time.DateTime
import org.junit.Assert._
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito._
import org.mockito.runners.MockitoJUnitRunner
import uk.co.telegraph.applereceipt.ITunesStatus._
import uk.co.telegraph.applereceipt.ValidateAppleReceiptTest._

import scalaj.http.HttpResponse

trait TestAppConfig {
  val Environment: String = "test"
  val Config: Config = ConfigFactory.load(s"application.$Environment.conf")
}

object ValidateAppleReceiptTest {
  val statusOK = 0
  val InvalidProductIdResponse = "{\"receipt\": {\"in_app\": [{\"product_id\": \"INVALID_PRODUCT_ID\"}]},\"status\": 0\n}"
  val MissingProductIdResponse = "{\"receipt\": {\"in_app\": []},\"status\": 0\n}"
  val ExpiredProductIdResponseIOS7 = "{\"receipt\": {\"in_app\": [  {\"quantity\":\"1\",\"product_id\":\"com.telegraph.ipad.2\",\"expires_date_ms\":\""+DateTime.now().minusMinutes(1).getMillis+"\"}]},\"status\": 0\n}"
  val ValidProductIdResponse = "{\"receipt\": {\"in_app\": [{\"product_id\": \"com.telegraph.ipad.2\"}]},\"status\": 0\n}"
  val ValidProductIdResponseIOS7 = "{\"receipt\": {\"in_app\": [  {\"quantity\":\"1\",\"product_id\":\"com.telegraph.ipad.2\",\"expires_date_ms\":\""+DateTime.now().plusMinutes(1).getMillis+"\"}]},\"status\": 0\n}"

  def apply(config: Config): ValidateAppleReceipt = new ValidateAppleReceipt(
    config.getString("app.apple.url"),
    config.getString("app.apple.password"),
    config.getString("app.apple.allowedsubscriptions")
  )
}

@RunWith(classOf[MockitoJUnitRunner])
class ValidateAppleReceiptTest extends TestAppConfig{

  @Mock val mockRequest:Receipt = null
  @Mock val mockHttpResponse: HttpResponse[String] = null

  class TestValidateAppleReceipt(val response:String) extends ValidateAppleReceipt(Config.getString("app.apple.url"), Config.getString("app.apple.password"), Config.getString("app.apple.allowedsubscriptions")) {
    override def callAppleUrl(appleUrl:String, iTunesReceipt: String):HttpResponse[String] = {
      when(mockHttpResponse.body).thenReturn(response)
      mockHttpResponse
    }
  }

  @Test
  def failOpenWhenITunesHttpCallIsUnsuccessful(): Unit = {
    when(mockHttpResponse.isSuccess).thenReturn(false)
    val validator = new TestValidateAppleReceipt("")
    val resultHolder = validator.validate(mockRequest)
    assertThat(resultHolder.response.get.getStatus, equalTo(Response.Status.NO_CONTENT.getStatusCode))
  }

  @Test
  def failedWhenErrorReadJson() {
    validateWithResponseBodyAndCheckForStatus(" {\"status\":" + ERR_READ_JSON.getCode + "}", BAD_REQUEST)
  }

  @Test
  def failedWhenErrorBadReceiptData() {
    validateWithResponseBodyAndCheckForStatus(" {\"status\":" + ERR_BAD_RECEIPT_DATA.getCode + "}", BAD_REQUEST)
  }

  @Test
  def failedWhenErrorNotAuth() {
    validateWithResponseBodyAndCheckForStatus(" {\"status\":" + ERR_NOT_AUTH.getCode + "}", UNAUTHORIZED)
  }

  @Test
  def failedWhenErrorBadSecret() {
    validateWithResponseBodyAndCheckForStatus(" {\"status\":" + ERR_BAD_SECRET.getCode + "}", FORBIDDEN)
  }

  @Test
  def failedWhenErrorServerDown() {
    validateWithResponseBodyAndCheckForStatus(" {\"status\":" + ERR_SERVER_DOWN.getCode + "}", NO_CONTENT)
  }

  @Test
  def failedWhenErrorSubExpired() {
    validateWithResponseBodyAndCheckForStatus(" {\"status\":" + ERR_SUB_EXPIRED.getCode + "}", FORBIDDEN)
  }

  @Test
  def failedWhenErrorSandboxReceipt() {
    validateWithResponseBodyAndCheckForStatus(" {\"status\":" + ERR_SANDBOX_RECEIPT.getCode + "}", NOT_ACCEPTABLE)
  }

  @Test
  def failedWhenErrorLiveReceipt() {
    validateWithResponseBodyAndCheckForStatus(" {\"status\":" + ERR_LIVE_RECEIPT.getCode + "}", NOT_ACCEPTABLE)
  }

  @Test
  def failedWhenNotAllowedSubscription() = {
    validateWithResponseBodyAndCheckForStatus(InvalidProductIdResponse, UNAUTHORIZED)
  }

  @Test
  def failedWhenMissingSubscription() = {
    validateWithResponseBodyAndCheckForStatus(MissingProductIdResponse, UNAUTHORIZED)
  }

  @Test
  def expiredResponseIOS7() = {
    validateWithResponseBodyAndCheckForStatus(ExpiredProductIdResponseIOS7, FORBIDDEN)
  }

  @Test
  def validResponse() = {
    validateWithResponseBodyAndCheckForStatus(ValidProductIdResponse, Response.Status.NO_CONTENT)
  }

  @Test
  def validResponseIOS7() = {
    validateWithResponseBodyAndCheckForStatus(ValidProductIdResponseIOS7, Response.Status.NO_CONTENT)
  }

  def validateWithResponseBodyAndCheckForStatus(appleResponseBody: String, responseStatus: Response.Status ):Unit = {
    when(mockHttpResponse.isSuccess).thenReturn(true)
    val validator = new TestValidateAppleReceipt(appleResponseBody)
    val resultHolder = validator.validate(mockRequest)
    assertEquals(responseStatus.getStatusCode, resultHolder.response.get.getStatus)
    assertEquals(resultHolder.nitroApiException, None)
  }

}

