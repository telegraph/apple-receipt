package uk.co.telegraph.applereceipt.test

import java.io.{InputStream, OutputStream}

import com.fasterxml.jackson.databind.{JsonMappingException, ObjectMapper}
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.Logger
import org.apache.commons.lang3.StringUtils
import Main._

trait AppConfig {
  val Environment: String = Option(System.getenv("ENVIRONMENT")) filter { x => x == "qa" || x == "prod" } getOrElse "dev"
  val Config: Config = ConfigFactory.load(s"application.$Environment.conf")
  val Clock:Clock = new SystemClock
}

object Main {
  val UNAUTHORIZED = ""
  val FORBIDDEN = ""
  val NO_CONTENT = ""
  val VERIFICATION_FAILED = "verification failed"
  val TOKEN_EXPIRED = "token expired"
  val DATA_SIGNATURE_IS_MISSING = "dataSignature is missing"
  val PURCHASE_DATA_IS_MISSING = "purchaseData is missing"
  private val KEY_FACTORY_ALGORITHM = "RSA"
  private val SIGNATURE_ALGORITHM = "SHA1withRSA"


  val OM: ObjectMapper = {
    import com.fasterxml.jackson.databind.ObjectMapper
    import com.fasterxml.jackson.module.scala.DefaultScalaModule
    new ObjectMapper().registerModule(new DefaultScalaModule)
  }

  val logger: Logger = Logger(classOf[Main])
}


class Main extends AppConfig {

  def validateGooglePlayReceipt(input: InputStream, output: OutputStream) {
    var response: ApiGatewayResponse = null
    try {
      val validator = ValidateGooglePlayReceipt(Config, Clock)

      val request = OM.readValue(input, classOf[ApiGatewayRequest])

      logger.debug(s"Got $request")

      if (StringUtils.isEmpty(request.body)) {
        logger.warn(s"No body found")
        throw new NitroApiException(400, "Body not found", "NBE0000")
      } else {
        val receiptFromRequest: GoogleReceipt = OM.readValue(request.body, classOf[GoogleReceipt])
        validator.validate(receiptFromRequest)
        response = ApiGatewayResponse(statusCode = 204)
      }
    }
    catch {
      case ne: NitroApiException =>
        logger.warn("Got api exception {}", ne.getErrorMessage)
        response = ApiGatewayResponse(statusCode = ne.statusCode, objectBody = ne)
      case je: JsonMappingException =>
        logger.warn("mapping exception {}", je.getMessage)
        val ne = new NitroApiException(400, "Invalid body", "NBE0000")
        response = ApiGatewayResponse(statusCode = ne.statusCode, objectBody = ne)
      case e: Throwable =>
        logger.error("Error {}", e.getMessage)
        val ne = new NitroApiException(500, "Internal Server Error", "NBE0000")
        response = ApiGatewayResponse(statusCode = ne.statusCode, objectBody = ne)
    }
    finally {
      logger.debug(s"Sent $response")
      OM.writeValue(output, response)
    }
  }
}


