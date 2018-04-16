package uk.co.telegraph.applereceipt

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, InputStream}

import com.fasterxml.jackson.databind.ObjectMapper
import com.typesafe.config.{Config, ConfigFactory}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}
import uk.co.telegraph.applereceipt.example.test.{ApiGatewayRequest, Clock, Main}
import uk.co.telegraph.applereceipt.example.{ApiGatewayResponse, Clock, Main}
import uk.co.telegraph.applereceipt.test.{ApiGatewayRequest, ApiGatewayResponse, Clock, Main}

@RunWith(classOf[JUnitRunner])
class MainTest extends FunSpec with Matchers{

  import MainTest._

  describe("Given a receipt validator"){

    it("When sending no body should return error"){
      val stream = outputStream()
      mainWithRealClock.validateGooglePlayReceipt(inputStream(apiGatewayRequestNoBody), stream)
      val response = OM.readValue(stream.toByteArray, classOf[ApiGatewayResponse])
      response.statusCode should equal(400)
    }

    it("When sending invalid body should return error"){
      val stream = outputStream()
      mainWithRealClock.validateGooglePlayReceipt(inputStream(apiGatewayRequestWithBody), stream)
      val response = OM.readValue(stream.toByteArray, classOf[ApiGatewayResponse])
      response.statusCode should equal(400)
    }


    it("When sending valid purchase data return 204"){
      val stream = outputStream()
      mainWithMockedClock.validateGooglePlayReceipt(inputStream(apiGatewayRequestWithBodyAndPurchaseData), stream)
      val response = OM.readValue(stream.toByteArray, classOf[ApiGatewayResponse])
      response.statusCode should equal(204)
    }

    it("When sending expired purchase data return 403"){
      val stream = outputStream()
      mainWithRealClock.validateGooglePlayReceipt(inputStream(apiGatewayRequestWithBodyAndPurchaseData), stream)
      val response = OM.readValue(stream.toByteArray, classOf[ApiGatewayResponse])
      response.statusCode should equal(403)
    }
  }
}

object MainTest{

  val OM: ObjectMapper = {
    import com.fasterxml.jackson.databind.ObjectMapper
    import com.fasterxml.jackson.module.scala.DefaultScalaModule
    new ObjectMapper().registerModule(new DefaultScalaModule)
  }

  val mainWithMockedClock:Main = new Main {
    override val Config: Config = ConfigFactory.load("application.tst.conf")
    override val Clock: Clock = new Clock {
      override def now(): Long = 1421149073319L
    }
  }

  val mainWithRealClock:Main = new Main {
    override val Config: Config = ConfigFactory.load("application.tst.conf")
  }

  val apiGatewayRequestNoBody:ApiGatewayRequest = ApiGatewayRequest("/resource","/resource","POST")
  val apiGatewayRequestWithBody:ApiGatewayRequest = ApiGatewayRequest("/resource","/resource","POST", body = "\"\"")

  val purchaseData = "{\"orderId\":\"12999763169054705758.1331818738639280\",\"packageName\":\"uk.co.telegraph.android\",\"productId\":\"uk.co.telegraph.monthly\",\"purchaseTime\":1436777741916,\"purchaseState\":0,\"developerPayload\":\"d34d662e-c17a-4c48-a66d-25829ec5ec91\",\"purchaseToken\":\"poneccalaalcdkcpokldjbhf.AO-J1Ow0Lqx7ouq_T4aoK4866oFqlFjlZiWHgTqMmvjRsFAg8jNELx8HRmoanNM0XJ8pX-6lbQtL1P5LrJnAeYFrXuXmTzj7BvV0QAU8zbpO1UUgxrrIJelnZ8U2HK-gg3m2z4nhS4n4\",\"autoRenewing\":true}"

  val DataSignatureValid = "IOEFbLlW2BL3ENtDwh/IMtUDJ9il8fhJvtH086XuBEJeGf7u/7I0xfOh/JhoaCthxttl1NUSvKRoYmTXR4aQlG/70qRjyo8039uVibOMDBwG21CO/v+F3FoR5M+MMe3B8D3XHfHMdcn64+OFVJ/2lFme37SVm9HpEcIZAeLKPHj+503pB8IdvOmAUSAzr4gPXeypVDs4wH58d20zM3m/1TKwoJHsld+wYLHpDrUcXssc1DGufwv7zhvJ7jdRsT6rSphV8BsN4NOyc6QAnJWVmhJKdqdjpxV+xpD7tGlgNoR9UA0NmE3EUrCSzBrgJJfuRvXQaOD5Wt3yDt6WcUjd5A=="
  val PurchaseDataValid = "{\"orderId\":\"12999763169054705758.1342290731777351\",\"packageName\":\"uk.co.telegraph.kindlefire\",\"productId\":\"co.uk.telegraph.subscription.monthly\",\"purchaseTime\":1421149073320,\"purchaseState\":0,\"developerPayload\":\"1001\",\"purchaseToken\":\"gdjlkgcnooiahijjhmljianc.AO-J1OwZ2qgOYIQ2quR7QUcn5jNKbVFqBAm6n2czmZ6i1Omt_tBouP4tMPyN3uNt1PUub3H1EQsFXoi6B7XcaBmsGw4g9AQjK_8xZ56ato56_mQmpR8Myb_TRGHWBGJn1_nGxI-VKfG-GbVUc7X4EI5023Fr-gI37w\",\"autoRenewing\":true}"
  val PublicKeyValid = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqP21xfFjga7WfO91BoqiCN40ePDgvOenJDekQwN8/i4JRAJh6fvMJ0iyMv9/k5twE6zZOk8RE6eSDynFgVhRvt303icyPBXY/hOLIT2dX85+N+gQ2L0/ld9dVz+61TXrwX2kMse8Bz7qhOo/Y7H05B+BTEnCGEpANOcEHkYiiJjyjeMCGsjIrPh61Kxk1H93ldP465CRSX/ftCxwzl57F6H61eR+SvN9Uw2CuOr7cRCLgfj57ab03Tz+bUas24+c5ls7DNkbrSdcBiHCoL+kd5gmP9P3WDjzDcSxYxlJFCQjXmjPr4YmZqA7KqqlPJOGjKHtpW/+75CBywoaF7Cg1QIDAQAB"


  val apiGatewayRequestWithBodyAndPurchaseData:ApiGatewayRequest = ApiGatewayRequest("/resource","/resource","POST", body = OM.writeValueAsString(GoogleReceipt(PurchaseDataValid, DataSignatureValid)))

  def inputStream(value:Any): InputStream = {
    val bos = new ByteArrayOutputStream()
    OM.writeValue(bos, value)
    new ByteArrayInputStream(bos.toByteArray)
  }

  def outputStream(): ByteArrayOutputStream = {
    new ByteArrayOutputStream()
  }

  def fromStream(bos: ByteArrayOutputStream):ApiGatewayResponse = {
    val bis = new ByteArrayInputStream(bos.toByteArray)
    OM.readValue(bis, classOf[ApiGatewayResponse])
  }

  val requestNoPurchaseData = "{\"resource\":\"/googleplay\",\"path\":\"/googleplay\",\"httpMethod\":\"POST\",\"headers\":{\"CloudFront-Is-SmartTV-Viewer\":\"false\",\"cache-control\":\"no-cache\",\"X-Forwarded-For\":\"89.120.104.182, 54.239.167.90\",\"Accept\":\"*/*\",\"X-Forwarded-Port\":\"443\",\"Via\":\"2.0 cd103c18819ef0db201c8a8cb9162bd2.cloudfront.net (CloudFront)\",\"content-type\":\"application/json\",\"origin\":\"chrome-extension://fhbjgbiflinjbdggehcddcbncdddomop\",\"postman-token\":\"bb599ddc-a4d6-7b30-d54b-7b90129bf74a\",\"CloudFront-Forwarded-Proto\":\"https\",\"X-Forwarded-Proto\":\"https\",\"CloudFront-Viewer-Country\":\"RO\",\"Accept-Language\":\"en-US,en;q=0.9,ro;q=0.8\",\"CloudFront-Is-Tablet-Viewer\":\"false\",\"Accept-Encoding\":\"gzip, deflate, br\",\"User-Agent\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36\",\"CloudFront-Is-Desktop-Viewer\":\"true\",\"Host\":\"vzh9db40tl.execute-api.eu-west-1.amazonaws.com\",\"CloudFront-Is-Mobile-Viewer\":\"false\",\"X-Amzn-Trace-Id\":\"Root=1-5a7c5c03-0021e8527069cbc958747030\",\"X-Amz-Cf-Id\":\"77-aUhLfSHfZjARu2sKw-xFwXB-Ru973KdTx-hHEWND-h5yZ3NKYXA==\"},\"queryStringParameters\":null,\"pathParameters\":null,\"stageVariables\":null,\"body\":\"{}\",\"isBase64Encoded\":false}"

}