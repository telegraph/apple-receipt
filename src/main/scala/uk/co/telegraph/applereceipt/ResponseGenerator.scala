package uk.co.telegraph.applereceipt

import java.util

import uk.co.telegraph.applereceipt.ITunesStatus.ITunesStatus

object ResponseGenerator extends Enumeration {
  private val errorCodeMap = new util.HashMap[Integer, ITunesStatus]

  def getErrorCodesForItunesResponse(itunesStatus: ITunesStatus): Nothing = errorCodeMap.get(itunesStatus)

  try errorCodeMap.put(ERR_READ_JSON, NBE1015)
  errorCodeMap.put(ERR_BAD_RECEIPT_DATA, NBE1016)
  errorCodeMap.put(ERR_NOT_AUTH, NBE1017)
  errorCodeMap.put(ERR_BAD_SECRET, NBE1018)
  errorCodeMap.put(ERR_SERVER_DOWN, NBE1019)
  errorCodeMap.put(ERR_SUB_EXPIRED, NBE1020)
  errorCodeMap.put(ERR_SANDBOX_RECEIPT, NBE1021)
  errorCodeMap.put(ERR_LIVE_RECEIPT, NBE1022)
}