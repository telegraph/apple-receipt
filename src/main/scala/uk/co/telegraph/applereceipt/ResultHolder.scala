package uk.co.telegraph.identity.services.api.service.camel.receipt

import javax.ws.rs.core.Response

import uk.co.telegraph.applereceipt.NitroApiException


object ResultHolder {
  def builder = new ResultHolder.Builder

  class Builder {
    private var nitroApiException: Option[NitroApiException] = Option(null)
    private var response:Option[Response] = Option(null)
    private var responseBody:Option[String] = Option(null)

    def nitroApiException(nitroApiException: NitroApiException): Unit = {
      this.nitroApiException = Option(nitroApiException)
    }

    def response(response: Response): Unit = {
      this.response = Option(response)
    }

    def responseBody(responseBody: String): Unit = {
      this.responseBody = Option(responseBody)
    }

    def build = new ResultHolder(nitroApiException, response, responseBody)
  }

}

class ResultHolder private(val nitroApiException: Option[NitroApiException], val response: Option[Response], val responseBody: Option[String]){
  def builder = new ResultHolder.Builder
}
