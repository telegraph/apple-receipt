package uk.co.telegraph.applereceipt

import javax.ws.rs.core.HttpHeaders.{ACCEPT, CONTENT_TYPE}
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner
import scala.collection.{JavaConversions, JavaConverters}
import scala.util.parsing.json.JSON
import scalaj.http.{Http, HttpResponse}
import uk.co.telegraph.applereceipt.ValidateAppleReceiptTest._

object ValidateAppleReceiptTest {

  val inAppdata1 = Map("product_id" -> "product_id_1", "expires_date_ms" -> "expire_date_1")
  val inAppData2 = Map("product_id" -> "product_id_2", "expires_date_ms" -> "expire_date_2")
  val inAppDataList = List(inAppdata1, inAppData2)
  val receiptMap = Map("in_app" -> inAppDataList)
  val responseMap = Map("receipt" -> receiptMap, "status" -> 0.0)
  val response:Option[Any] = Option.apply(responseMap)

}

@RunWith(classOf[MockitoJUnitRunner])
class ValidateAppleReceiptTest {

  @Test
  def failOpenWhenNon200ITunesHttpResponseCode() {

  }

  @Test
  def failedWhenErrorReadJson() {
//    iTuneStatusTest(ERR_READ_JSON, " {\"status\":" + ERR_READ_JSON.getCode + "}")
  }

  @Test
  def failedWhenErrorBadReceiptData() {
//    iTuneStatusTest(ERR_BAD_RECEIPT_DATA, " {\"status\":" + ERR_BAD_RECEIPT_DATA.getCode + "}")
  }

  @Test
  def failedWhenErrorNotAuth() {
//    iTuneStatusTest(ERR_NOT_AUTH, " {\"status\":" + ERR_NOT_AUTH.getCode + "}")
  }

  @Test
  def failedWhenErrorBadSecret() {
//    iTuneStatusTest(ERR_BAD_SECRET, " {\"status\":" + ERR_BAD_SECRET.getCode + "}")
  }

  @Test
  def failedWhenErrorServerDown() {
//    iTuneStatusTest(ERR_SERVER_DOWN, " {\"status\":" + ERR_SERVER_DOWN.getCode + "}")
  }

  @Test
  def failedWhenErrorSubExpired() {
//    iTuneStatusTest(ERR_SUB_EXPIRED, " {\"status\":" + ERR_SUB_EXPIRED.getCode + "}")
  }

  @Test
  def failedWhenErrorSandboxReceipt() {
//    iTuneStatusTest(ERR_SANDBOX_RECEIPT, " {\"status\":" + ERR_SANDBOX_RECEIPT.getCode + "}")
  }

  @Test
  def failedWhenErrorLiveReceipt() {
//    iTuneStatusTest(ERR_LIVE_RECEIPT, " {\"status\":" + ERR_LIVE_RECEIPT.getCode + "}")
  }

  @Test
  def failedWhenNotAllowedSubscription() = {
//    mockEndpoint = setupMockEndpoint(CALL_APPLE_SERVICE_ROUTE_ID, AppleEndpoint, InvalidProductIdResponse, headers(Response.Status.OK.getStatusCode))

//    iTuneStatusTest(ERR_NOT_AUTH, InvalidProductIdResponse)
  }

  @Test
  def failedWhenMissingSubscription() = {
//    mockEndpoint = setupMockEndpoint(CALL_APPLE_SERVICE_ROUTE_ID, AppleEndpoint, MissingProductIdResponse, headers(Response.Status.OK.getStatusCode))

//    iTuneStatusTest(ERR_NOT_AUTH, MissingProductIdResponse)
  }

  @Test
  def expiredResponseIOS7() = {
//    mockEndpoint = setupMockEndpoint(CALL_APPLE_SERVICE_ROUTE_ID, AppleEndpoint, ExpiredProductIdResponseIOS7, headers(Response.Status.OK.getStatusCode))

//    iTuneStatusTest(ERR_SUB_EXPIRED, ExpiredProductIdResponseIOS7)
  }

  @Test
  def validResponse() = {
//    mockEndpoint = setupMockEndpoint(CALL_APPLE_SERVICE_ROUTE_ID, AppleEndpoint, ValidProductIdResponse, headers(Response.Status.OK.getStatusCode))

//    val response = this.template.requestBody(APPLE_RECEIPT_VALIDATOR_ENDPOINT, BuildReceiptDataForInput, classOf[Response])
//    assertThat(response.getStatus, equalTo(Response.Status.NO_CONTENT.getStatusCode))
//    assertMockEndpointsSatisfied()
  }

  @Test
  def validResponseIOS7() = {
//    mockEndpoint = setupMockEndpoint(CALL_APPLE_SERVICE_ROUTE_ID, AppleEndpoint, ValidProductIdResponseIOS7, headers(Response.Status.OK.getStatusCode))

//    val response = this.template.requestBody(APPLE_RECEIPT_VALIDATOR_ENDPOINT, BuildReceiptDataForInput, classOf[Response])
//    assertThat(response.getStatus, equalTo(Response.Status.NO_CONTENT.getStatusCode))
//    assertMockEndpointsSatisfied()
  }


//  def iTuneStatusTest(itunesStatus: ITunesStatus, itunesResponse: String) = {
//    mockEndpoint = setupMockEndpoint(CALL_APPLE_SERVICE_ROUTE_ID, AppleEndpoint, itunesResponse, headers(Response.Status.OK.getStatusCode))
//    val response = this.template.requestBody(APPLE_RECEIPT_VALIDATOR_ENDPOINT, BuildReceiptDataForInput, classOf[Response])
//    val status = if (itunesStatus.name().equals(ITunesStatus.ERR_SERVER_DOWN.name())) Status.NO_CONTENT else getHttpCodeForItunesResponseCode(itunesStatus)
//    assertThat(response.getStatus, equalTo(status.getStatusCode))
//    assertMockEndpointsSatisfied()
//  }


  @Test
  def testHttpRequest() = {
    val requestBody = "{\"receipt-data\": \"eyJzaWduYXR1cmUiID0gIkFoaHEwbmFxaG01ci8wSUNuYk9hVThCeVBLNkRja2ZsanRCMDNnZUh4dk0ybEVjVkdqK2NVM1lnWGZ0RkZCZ2lFYkR1NGdoYXFVWFRqRzlpc25Zeit6VWFhTXRUZDJ6YnNLbFhIMitzYm1ZaC9tY2M2eWt3dVFkaFZsOWZKYkxNaFVXWHNTUlIxVlVjQlE4TkxjVGg5ZHNXOTVTREcxdG9DQk45cWM0L01CZlVBQUFEVnpDQ0ExTXdnZ0k3b0FNQ0FRSUNDQnVwNCtQQWhtL0xNQTBHQ1NxR1NJYjNEUUVCQlFVQU1IOHhDekFKQmdOVkJBWVRBbFZUTVJNd0VRWURWUVFLREFwQmNIQnNaU0JKYm1NdU1TWXdKQVlEVlFRTERCMUJjSEJzWlNCRFpYSjBhV1pwWTJGMGFXOXVJRUYxZEdodmNtbDBlVEV6TURFR0ExVUVBd3dxUVhCd2JHVWdhVlIxYm1WeklGTjBiM0psSUVObGNuUnBabWxqWVhScGIyNGdRWFYwYUc5eWFYUjVNQjRYRFRFME1EWXdOekF3TURJeU1Wb1hEVEUyTURVeE9ERTRNekV6TUZvd1pERWpNQ0VHQTFVRUF3d2FVSFZ5WTJoaGMyVlNaV05sYVhCMFEyVnlkR2xtYVdOaGRHVXhHekFaQmdOVkJBc01Fa0Z3Y0d4bElHbFVkVzVsY3lCVGRHOXlaVEVUTUJFR0ExVUVDZ3dLUVhCd2JHVWdTVzVqTGpFTE1Ba0dBMVVFQmhNQ1ZWTXdnWjh3RFFZSktvWklodmNOQVFFQkJRQURnWTBBTUlHSkFvR0JBTW1URXVMZ2ppbUx3Ukp4eTFvRWYwZXNVTkRWRUllNndEc25uYWwxNGhOQnQxdjE5NVg2bjkzWU83Z2kzb3JQU3V4OUQ1NTRTa01wK1NheWc4NGxUYzM2MlV0bVlMcFduYjM0bnF5R3g5S0JWVHk1T0dWNGxqRTFPd0Mrb1RuUk0rUUxSQ21lTnhNYlBaaFM0N1QrZVp0REVoVkI5dXNrMytKTTJDb2dmd283QWdNQkFBR2pjakJ3TUIwR0ExVWREZ1FXQkJTSmFFZU51cTlEZjZaZk42OEZlK0kydTIyc3NEQU1CZ05WSFJNQkFmOEVBakFBTUI4R0ExVWRJd1FZTUJhQUZEWWQ2T0tkZ3RJQkdMVXlhdzdYUXd1UldFTTZNQTRHQTFVZER3RUIvd1FFQXdJSGdEQVFCZ29xaGtpRzkyTmtCZ1VCQkFJRkFEQU5CZ2txaGtpRzl3MEJBUVVGQUFPQ0FRRUFlYUpWMlU1MXJ4ZmNxQUFlNUMyL2ZFVzhLVWw0aU80bE11dGE3TjZYelAxcFpJejFOa2tDdElJd2V5Tmo1VVJZSEsrSGpSS1NVOVJMZ3VObDBua2Z4cU9iaU1ja3dSdWRLU3E2OU5JbnJaeUNENjZSNEs3N25iOWxNVEFCU1NZbHNLdDhvTnRsaGdSLzFralNTUlFjSGt0c0RjU2lRR0tNZGtTbHA0QXlYZjd2bkhQQmU0eUN3WVYyUHBTTjA0a2JvaUozcEJseHNHd1YvWmxMMjZNMnVlWUhLWUN1WGhkcUZ3eFZnbTUyaDNvZUpPT3Qvdlk0RWNRcTdlcUhtNm0wM1o5YjdQUnpZTTJLR1hIRG1PTWs3dkRwZU1WbExEUFNHWXoxK1Uzc0R4SnplYlNwYmFKbVQ3aW16VUtmZ2dFWTd4eGY0Y3pmSDB5ajV3TnpTR1RPdlE9PSI7ICJwdXJjaGFzZS1pbmZvIiA9ICJld29KSW05eWFXZHBibUZzTFhCMWNtTm9ZWE5sTFdSaGRHVXRjSE4wSWlBOUlDSXlNREUxTFRFeExUSXpJREE0T2pNeU9qSTNJRUZ0WlhKcFkyRXZURzl6WDBGdVoyVnNaWE1pT3dvSkluVnVhWEYxWlMxcFpHVnVkR2xtYVdWeUlpQTlJQ0psTkRjME1qVmtaamRtWWpaaFlqaGpNMk0zWlRVM016QmpNMkUzWldJMVlqWTFOak00TWpOa0lqc0tDU0p2Y21sbmFXNWhiQzEwY21GdWMyRmpkR2x2YmkxcFpDSWdQU0FpTVRBd01EQXdNREU0TVRRNU1qVTFOeUk3Q2draVluWnljeUlnUFNBaU15STdDZ2tpZEhKaGJuTmhZM1JwYjI0dGFXUWlJRDBnSWpFd01EQXdNREF4T0RFME9USTFOVGNpT3dvSkluRjFZVzUwYVhSNUlpQTlJQ0l4SWpzS0NTSnZjbWxuYVc1aGJDMXdkWEpqYUdGelpTMWtZWFJsTFcxeklpQTlJQ0l4TkRRNE1qazJNelEzTVRNNElqc0tDU0oxYm1seGRXVXRkbVZ1Wkc5eUxXbGtaVzUwYVdacFpYSWlJRDBnSWpWRE1qYzFRalZCTFRORk5qWXROREF5TmkwNU5USXpMVGcwTlVJeU1qVTNOVFZHUXlJN0Nna2ljSEp2WkhWamRDMXBaQ0lnUFNBaWNIVnlZMmhoYzJWeUxtTnZibk4xYldGaWJHVkdaV0YwZFhKbElqc0tDU0pwZEdWdExXbGtJaUE5SUNJeE1EWXhOVFUzTkRnMElqc0tDU0ppYVdRaUlEMGdJbU52YlM1bGN5NVFkWEpqYUdGelpYSWlPd29KSW5CMWNtTm9ZWE5sTFdSaGRHVXRiWE1pSUQwZ0lqRTBORGd5T1RZek5EY3hNemdpT3dvSkluQjFjbU5vWVhObExXUmhkR1VpSUQwZ0lqSXdNVFV0TVRFdE1qTWdNVFk2TXpJNk1qY2dSWFJqTDBkTlZDSTdDZ2tpY0hWeVkyaGhjMlV0WkdGMFpTMXdjM1FpSUQwZ0lqSXdNVFV0TVRFdE1qTWdNRGc2TXpJNk1qY2dRVzFsY21sallTOU1iM05mUVc1blpXeGxjeUk3Q2draWIzSnBaMmx1WVd3dGNIVnlZMmhoYzJVdFpHRjBaU0lnUFNBaU1qQXhOUzB4TVMweU15QXhOam96TWpveU55QkZkR012UjAxVUlqc0tmUT09IjsiZW52aXJvbm1lbnQiID0gIlNhbmRib3giOyJwb2QiID0gIjEwMCI7InNpZ25pbmctc3RhdHVzIiA9ICIwIjt9\", \"password\" : \"77e8c4eb232441c7a6e1956208c9caaa\"}"
    //    val result:HttpResponse[String] = Http("https://buy.itunes.apple.com/verifyReceipt").postData(requestBody)
    val result:HttpResponse[String] = Http("https://sandbox.itunes.apple.com/verifyReceipt").postData(requestBody)
      .header(CONTENT_TYPE, APPLICATION_JSON)
      .header(ACCEPT, APPLICATION_JSON)
      .timeout(connTimeoutMs = 80000, readTimeoutMs = 80000)
      .execute()

    val jsonObject = JSON.parseFull(result.body)
    val iTunesResponse = JsonUtil.fromJson[ITunesResponse](jsonObject.get.toString)
    System.out.println(iTunesResponse)
  }

  @Test
  def testCreateiTunesResponse() = {
    System.out.println(getITunesResponse(response))

  }

  def getITunesResponse(jsonObject: Option[Any]):ITunesResponse = {
    val fields = jsonObject.get.asInstanceOf[Map[String, Any]]
    val statusCode = fields.get("status").get.asInstanceOf[Double].toInt
    val receiptData = fields.get("receipt")

    val iTunesResponse = new ITunesResponse
    iTunesResponse.setStatus(statusCode)

    if (!receiptData.isEmpty) {
      val receiptDataMap = receiptData.get.asInstanceOf[Map[String, Any]]

      val inApp = receiptDataMap.get("in_app")
      val inAppList = List[InAppData]()
      if (!inApp.isEmpty) {
        val inAppMapList = inApp.get.asInstanceOf[List[Map[String, Any]]]
        for (inAppItem <- inAppMapList) {
          val inAppData: InAppData = new InAppData
          val productId = inAppItem.get("product_id")
          if (!productId.isEmpty) {
            val productIdValue = productId.get.asInstanceOf[String]
            inAppData.setProductId(productIdValue)
            val expiredDate = inAppItem.get("expires_date_ms")
            if (!expiredDate.isEmpty) {
              val expiredDateValue = expiredDate.get.asInstanceOf[String]
              inAppData.setExpiresDateMs(expiredDateValue)
            }
          }
          inAppList.::(inAppData)
        }
      }
      val iTunesReceiptData = new ITunesReceiptData
      iTunesReceiptData.setInAppData(JavaConversions.seqAsJavaList(inAppList))
      iTunesResponse.setReceipt(iTunesReceiptData)
    }

    iTunesResponse
  }
}

