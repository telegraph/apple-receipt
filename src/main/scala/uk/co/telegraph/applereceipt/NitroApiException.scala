package uk.co.telegraph.applereceipt

import com.fasterxml.jackson.annotation.{JsonAutoDetect, JsonProperty}

@JsonAutoDetect(
  creatorVisibility = JsonAutoDetect.Visibility.NONE,
  fieldVisibility = JsonAutoDetect.Visibility.NONE,
  getterVisibility = JsonAutoDetect.Visibility.NONE,
  isGetterVisibility = JsonAutoDetect.Visibility.NONE,
  setterVisibility = JsonAutoDetect.Visibility.NONE
)
class NitroApiException(val statusCode: Int, val errorMessage: String, val errorCode: String) extends Exception {

  @JsonProperty("statusCode")
  def getStatusCode: String ={
    statusCode.toString
  }

  @JsonProperty("errorMessage")
  def getErrorMessage: String = {
    errorMessage
  }

  @JsonProperty("errorCode")
  def getErrorCode: String ={
    errorCode
  }
}