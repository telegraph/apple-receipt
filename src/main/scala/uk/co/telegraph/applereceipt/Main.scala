package uk.co.telegraph.applereceipt

import java.io.{InputStream, OutputStream}

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.fasterxml.jackson.databind.{JsonMappingException, ObjectMapper}
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.commons.lang3.StringUtils
import uk.co.telegraph.applereceipt.Main._
import uk.co.telegraph.applereceipt.model.{ApiGatewayRequest, ApiGatewayResponse, Receipt}

trait AppConfig {
  val Environment: String = Option(System.getenv("ENVIRONMENT")) filter { x => x == "qa" || x == "prod" } getOrElse "dev"
  val Config: Config = ConfigFactory.load(s"application.$Environment.conf")
}

object Main {
  val UNAUTHORIZED = ""
  val FORBIDDEN = ""
  val NO_CONTENT = ""
  val VERIFICATION_FAILED = "verification failed"
  val TOKEN_EXPIRED = "token expired"
  val DATA_SIGNATURE_IS_MISSING = "dataSignature is missing"


  val OM: ObjectMapper = {
    import com.fasterxml.jackson.databind.ObjectMapper
    import com.fasterxml.jackson.module.scala.DefaultScalaModule
    new ObjectMapper().registerModule(new DefaultScalaModule)
  }

  val logger: Logger = LoggerFactory.getLogger(classOf[Main])
}

class Main extends AppConfig {

  def validateAppleReceipt(input: InputStream, output: OutputStream) {
    var response: ApiGatewayResponse = null
    LoggingUtil.initLog()
    try {
      logger.warn(s"Environment $Environment")
      val validator = ValidateAppleReceipt(Config)
      val request = OM.readValue(input, classOf[ApiGatewayRequest])
      logger.warn(s"Got request $request")

      if (StringUtils.isEmpty(request.body)) {
        logger.warn(s"No body found")
        throw new NitroApiException(400, "Body not found", "NBE0000")
      } else {
        val receipt: Receipt = OM.readValue(request.body, classOf[Receipt])
        val resultHolder = validator.validate(receipt)
        response = ApiGatewayResponse(statusCode = resultHolder.response.get.getStatus, objectBody = resultHolder.responseBody.get)
      }
    }
    catch {
      case ne: NitroApiException =>
        logger.warn(" Got api exception {}", ne.getErrorMessage)
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


