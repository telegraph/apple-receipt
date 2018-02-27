package uk.co.telegraph.googleplayreceipt

trait Clock {
  def now(): Long
}

class SystemClock extends Clock {
  override def now():Long = System.currentTimeMillis()
  val x = "{\"purchaseData\": \"{\\\"orderId\\\":\\\"12999763169054705758.1342290731777351\\\",\\\"packageName\\\":\\\"uk.co.telegraph.kindlefire\\\",\\\"productId\\\":\\\"co.uk.telegraph.subscription.monthly\\\",\\\"purchaseTime\\\":1421149073320,\\\"purchaseState\\\":0,\\\"developerPayload\\\":\\\"1001\\\",\\\"purchaseToken\\\":\\\"gdjlkgcnooiahijjhmljianc.AO-J1OwZ2qgOYIQ2quR7QUcn5jNKbVFqBAm6n2czmZ6i1Omt_tBouP4tMPyN3uNt1PUub3H1EQsFXoi6B7XcaBmsGw4g9AQjK_8xZ56ato56_mQmpR8Myb_TRGHWBGJn1_nGxI-VKfG-GbVUc7X4EI5023Fr-gI37w\\\",\\\"autoRenewing\\\":true}\",\"dataSignature\": \"IOEFbLlW2BL3ENtDwh/IMtUDJ9il8fhJvtH086XuBEJeGf7u/7I0xfOh/JhoaCthxttl1NUSvKRoYmTXR4aQlG/70qRjyo8039uVibOMDBwG21CO/v+F3FoR5M+MMe3B8D3XHfHMdcn64+OFVJ/2lFme37SVm9HpEcIZAeLKPHj+503pB8IdvOmAUSAzr4gPXeypVDs4wH58d20zM3m/1TKwoJHsld+wYLHpDrUcXssc1DGufwv7zhvJ7jdRsT6rSphV8BsN4NOyc6QAnJWVmhJKdqdjpxV+xpD7tGlgNoR9UA0NmE3EUrCSzBrgJJfuRvXQaOD5Wt3yDt6WcUjd5A==\"}"
}