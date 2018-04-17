package uk.co.telegraph.applereceipt

import java.util.concurrent.TimeUnit

import org.apache.http.client.config.RequestConfig
import org.apache.http.config.SocketConfig
import org.apache.http.impl.client.HttpClientBuilder

 object AppleHttpClientConfigurer {
  ("{{apple.wait.timeout}}")
   private val appleWaitTimeout = 0

  ("{{apple.evict.idle.connections}}")
   private val appleEvictIdleConnections = 0L
}

class AppleHttpClientConfigurer extends HttpClientBuilder {
  def configureHttpClient(httpClientBuilder: HttpClientBuilder): Unit = httpClientBuilder
    .setDefaultRequestConfig(RequestConfig.custom.setConnectTimeout(AppleHttpClientConfigurer.appleWaitTimeout).setSocketTimeout(AppleHttpClientConfigurer.appleWaitTimeout).build)
    .evictIdleConnections(AppleHttpClientConfigurer.appleEvictIdleConnections, TimeUnit.MILLISECONDS)
    .setDefaultSocketConfig(SocketConfig.custom.setSoTimeout(AppleHttpClientConfigurer.appleWaitTimeout).build)
    .build
}