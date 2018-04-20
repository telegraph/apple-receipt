package uk.co.telegraph.applereceipt

object ITunesStatus extends Enumeration {
  var codeToStatusMapping: java.util.Map[Integer, Status] = _
  case class Status(code: Integer, description: String) extends super.Val {
    def getCode: Integer = {code}
    def getDescription = {description}

    def getCodeString:String = {Integer.toString(code)}

    override
    def toString: String = String.format("ITunesStatus{code=%s, description=%s}", code, description)

  }
  implicit def valueToStatusVal(x: Value): Status = x.asInstanceOf[Status]

  def getStatus(i: Integer): Status = {
    if (codeToStatusMapping == null) initMapping()
    codeToStatusMapping.get(i)
  }

  def initMapping(): Unit = {
    codeToStatusMapping = new java.util.HashMap[Integer, Status]
    for (s <- values) {
      codeToStatusMapping.put(s.code, s)
    }
  }

  val OK = Status(0, "Verified OK.")
  val ERR_READ_JSON = Status(21000, "The App Store could not read the JSON object you provided.")
  val ERR_BAD_RECEIPT_DATA = Status (21002, "The data in the receipt-data property was malformed or missing.")
  val ERR_NOT_AUTH = Status (21003, "The receipt could not be authenticated.")
  val ERR_BAD_SECRET = Status(21004, "The shared secret you provided does not match the shared secret on file for your account.")
  val ERR_SERVER_DOWN = Status(21005, "The receipt server is not currently available.")
  val ERR_SUB_EXPIRED = Status(21006, "This receipt is valid but the subscription has expired. When this status code is returned to your server, the receipt data is also decoded and returned as part of the response.")
  val ERR_SANDBOX_RECEIPT = Status(21007, "This receipt is from the test environment, but it was sent to the production environment for verification. Send it to the test environment instead.")
  val ERR_LIVE_RECEIPT = Status(21008, "This receipt is from the production environment, but it was sent to the test environment for verification. Send it to the production environment instead.")

}
