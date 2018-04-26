package uk.co.telegraph.applereceipt.model

import java.nio.charset.StandardCharsets
import java.util.Base64

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import uk.co.telegraph.applereceipt.Main

@JsonIgnoreProperties(ignoreUnknown = true)
class ApiGatewayRequest(val resource: String,
                        val path: String,
                        val httpMethod: String,
                        val headers: Map[String, String],
                        val queryStringParameters: Map[String, String],
                        val pathParameters: Map[String, String],
                        val stageVariables: Map[String, String],
                        val body: String,
                        val isBase64Encoded: Boolean) {
  override def toString: String = Main.OM.writeValueAsString(this)
}

object ApiGatewayRequest {
  def apply(resource: String = null,
            path: String = null,
            httpMethod: String = null,
            headers: Map[String, String] = Map.empty,
            queryStringParameters: Map[String, String] = Map.empty,
            pathParameters: Map[String, String] = Map.empty,
            stageVariables: Map[String, String] = Map.empty,
            body: String = null,
            isBase64Encoded: Boolean = false): ApiGatewayRequest = {
    var decodedBody = body
    if(isBase64Encoded && body != null) {
      decodedBody = new String(Base64.getDecoder.decode(body), StandardCharsets.UTF_8)
    }
    new ApiGatewayRequest(resource, path, httpMethod, headers, queryStringParameters, pathParameters, stageVariables, decodedBody, isBase64Encoded)
  }
}