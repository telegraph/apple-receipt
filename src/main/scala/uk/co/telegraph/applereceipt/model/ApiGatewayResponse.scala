package uk.co.telegraph.applereceipt.model

import java.nio.charset.StandardCharsets
import java.util.Base64

import com.fasterxml.jackson.core.JsonProcessingException
import uk.co.telegraph.applereceipt.Main

object ApiGatewayResponse {

  def apply(statusCode: Int = 200, headers:Map[String, String] = Map.empty, rawBody:String = null, objectBody:Object = null, binaryBody:Array[Byte] = null, base64Encoded:Boolean = false): ApiGatewayResponse = {
    var body:String = null
    if (rawBody != null) body = rawBody
    else if (objectBody != null) try
      body = Main.OM.writeValueAsString(objectBody)
    catch {
      case e: JsonProcessingException =>
        throw new RuntimeException(e)
    }
    else if (binaryBody != null) body = new String(Base64.getEncoder.encode(binaryBody), StandardCharsets.UTF_8)

    new ApiGatewayResponse(statusCode, body, headers, base64Encoded)
  }

}

class ApiGatewayResponse(val statusCode: Int, val body: String, val headers: Map[String, String], val isBase64Encoded: Boolean) {
  override def toString: String = Main.OM.writeValueAsString(this)
}