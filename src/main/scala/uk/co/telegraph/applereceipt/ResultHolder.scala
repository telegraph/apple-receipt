package uk.co.telegraph.identity.services.api.service.camel.receipt

import com.google.common.base.Optional
import javax.ws.rs.core.Response

import uk.co.telegraph.applereceipt.NitroApiException


object ResultHolder {
  def builder = new ResultHolder.Builder

  class Builder {
    private var nitroApiException: Option[NitroApiException] = Option(null)
    private var response:Option[Response] = Option(null)

    def nitroApiException(nitroApiException: NitroApiException): Unit = {
      this.nitroApiException = Option(nitroApiException)
    }

    def response(response: Response): Unit = {
      this.response = Option(response)
    }

    def build = new ResultHolder(nitroApiException, response)
  }

}

class ResultHolder private(val nitroApiException: Option[NitroApiException], val response: Option[Response]){
  def builder = new ResultHolder.Builder
}
