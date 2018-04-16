package uk.co.telegraph.applereceipt

import java.util

object ITunesStatus extends Enumeration {
  type ITunesStatus = Val
  val OK, ERR_READ_JSON, ERR_BAD_RECEIPT_DATA, ERR_NOT_AUTH, ERR_BAD_SECRET, ERR_SERVER_DOWN, ERR_SUB_EXPIRED, ERR_SANDBOX_RECEIPT, ERR_LIVE_RECEIPT = Value
  var codeToStatusMapping: util.Map[Integer, String] = _
  var code: Integer = 0
  var description: String = _

  this (code: Integer, description: String) {
    this.code = code
    this.description = description
  }

  def getStatus(i: Integer): ITunesStatus = {
    if (codeToStatusMapping == null) initMapping()
    codeToStatusMapping.get(i)
  }

  def initMapping(): Unit = {
    codeToStatusMapping = new util.HashMap[Integer, String]
    for (s <- values) {
      //codeToStatusMapping.put(s.code, s)
      codeToStatusMapping.put(code, ITunesStatus.description)
    }
  }

  def getCode: Int = {
    code
  }

  override
  def toString: String = String.format("ItunesState{code=%s, description=%s}", code, description)

  def getCodeString: String = {
    Integer.toString(code)
  }

  def getDescription: String = {
    description
  }
}