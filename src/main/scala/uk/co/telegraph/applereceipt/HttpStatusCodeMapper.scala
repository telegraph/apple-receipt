package uk.co.telegraph.applereceipt

import com.google.common.collect.ImmutableMap
import uk.co.telegraph.applereceipt.ITunesStatus.Status
import javax.ws.rs.core.Response

object HttpStatusCodeMapper extends Enumeration {
  private val httpStatusCodeMap = ImmutableMap.builder[Status, Response.Status]
    .put(ITunesStatus.OK, Response.Status.OK)
    .put(ITunesStatus.ERR_READ_JSON, Response.Status.BAD_REQUEST)
    .put(ITunesStatus.ERR_BAD_RECEIPT_DATA, Response.Status.BAD_REQUEST)
    .put(ITunesStatus.ERR_NOT_AUTH, Response.Status.UNAUTHORIZED)
    .put(ITunesStatus.ERR_BAD_SECRET, Response.Status.FORBIDDEN)
    .put(ITunesStatus.ERR_SERVER_DOWN, Response.Status.SERVICE_UNAVAILABLE)
    .put(ITunesStatus.ERR_SUB_EXPIRED, Response.Status.FORBIDDEN)
    .put(ITunesStatus.ERR_SANDBOX_RECEIPT, Response.Status.NOT_ACCEPTABLE)
    .put(ITunesStatus.ERR_LIVE_RECEIPT, Response.Status.NOT_ACCEPTABLE).build

  def getHttpCodeForItunesResponseCode(itunesResponseCode: Status): Response.Status = httpStatusCodeMap.get(itunesResponseCode)
}

final class HttpStatusCodeMapper private() {
}