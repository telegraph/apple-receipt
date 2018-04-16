package uk.co.telegraph.applereceipt

import com.google.common.collect.ImmutableMap
import uk.co.telegraph.applereceipt.ITunesStatus._

object HttpStatusCodeMapper {
  private val httpStatusCodeMap = ImmutableMap.builder[ITunesStatus, Nothing]
    .put(ITunesStatus.OK, Response.Status.OK)
    .put(ERR_READ_JSON, Response.Status.BAD_REQUEST)
    .put(ERR_BAD_RECEIPT_DATA, BAD_REQUEST)
    .put(ERR_NOT_AUTH, UNAUTHORIZED)
    .put(ERR_BAD_SECRET, FORBIDDEN)
    .put(ERR_SERVER_DOWN, SERVICE_UNAVAILABLE)
    .put(ERR_SUB_EXPIRED, Response.Status.FORBIDDEN)
    .put(ERR_SANDBOX_RECEIPT, Response.Status.NOT_ACCEPTABLE)
    .put(ERR_LIVE_RECEIPT, Response.Status.NOT_ACCEPTABLE).build

  def getHttpCodeForItunesResponseCode(itunesResponseCode: ITunesStatus): Nothing = httpStatusCodeMap.get(itunesResponseCode)
}

final class HttpStatusCodeMapper private() {
}