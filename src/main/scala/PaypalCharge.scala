import com.google.gson.*

import java.io._
import java.net.HttpURLConnection
import java.net.URL
import java.util._


object PaypalCharge {

  val baseUrl = "https://api-m.sandbox.paypal.com"
  val tokenUrl = s"$baseUrl/v1/oauth2/token"
  val endpoint = s"$baseUrl/v1/reporting/transactions"
  val clientId: String = ???

  val clientSecret: String = ???

  def getAuthToken(): String = {
    val credentials: String = clientId + ":" + clientSecret
    val encodedCredentials: String = Base64.getEncoder.encodeToString(credentials.getBytes)

    val httpConn: HttpURLConnection = new URL(tokenUrl).openConnection.asInstanceOf[HttpURLConnection]
    httpConn.setRequestMethod("POST")
    httpConn.setRequestProperty("Accept", "application/json")
    httpConn.setRequestProperty("Accept-Language", "en_US")
    httpConn.setRequestProperty("Authorization", "Basic " + encodedCredentials)
    httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
    httpConn.setDoOutput(true)
    val out = httpConn.getOutputStream
    out.write("grant_type=client_credentials".getBytes)
    out.flush
    out.close
    println(httpConn.getResponseCode)
    val responseStream: InputStream = if (httpConn.getResponseCode / 100 == 2) httpConn.getInputStream
    else httpConn.getErrorStream
    val s = new Scanner(responseStream).useDelimiter("\\A")
    val sb = new StringBuilder
    while (s.hasNext)
      val t = s.next
      sb.append(t)
    return sb.toString()
  }

  def extractAccessToken(tokenString: String): String = {
    val gson = new Gson()
    val json: JsonObject = gson.fromJson(tokenString, classOf[JsonObject])

    val accessToken: String = json.get("access_token").getAsString
    return accessToken
  }
  @throws[IOException]
  def getCharges(accessToken: String): String = {
    val params = "start_date=2024-08-28T00:00:00-0700&end_date=2024-09-12T23:59:59-0700&fields=all"
    val url = new URL(s"${endpoint}?${params}")
    println(url)
    val httpConn: HttpURLConnection = url.openConnection.asInstanceOf[HttpURLConnection]
    httpConn.setRequestMethod("GET")
    httpConn.setRequestProperty("Authorization", "Bearer " + accessToken)
    httpConn.setRequestProperty("Content-Type", "application/json")
    val responseStream: InputStream = if (httpConn.getResponseCode / 100 == 2) httpConn.getInputStream
    else httpConn.getErrorStream
    val s = new Scanner(responseStream).useDelimiter("\\A")
    val sb = new StringBuilder
    while (s.hasNext)
      val t = s.next
      sb.append(t)
    return sb.toString()
  }
  
  // https://developer.paypal.com/docs/checkout/apm/reference/subscribe-to-webhooks/
  def createWebhook(accessToken: String): String = {
    val url = new URL("https://api-m.sandbox.paypal.com/v1/notifications/webhooks")
    val httpConn: HttpURLConnection = url.openConnection.asInstanceOf[HttpURLConnection]
    httpConn.setRequestMethod("POST")

    httpConn.setRequestProperty("Content-Type", "application/json")
    httpConn.setRequestProperty("Authorization", "Bearer " + accessToken)

    httpConn.setDoOutput(true)
    val writer = new OutputStreamWriter(httpConn.getOutputStream)
    val body =
      s"""
         |{
         |  "url": "https://0773-47-145-224-148.ngrok-free.app/webhook",
         |  "event_types": [
         |    {
         |      "name": "CHECKOUT.ORDER.APPROVED"     
         |    },
         |    {
         |      "name": "CHECKOUT.PAYMENT-APPROVAL.REVERSED"
         |    },
         |    {
         |      "name": "PAYMENT.CAPTURE.PENDING"
         |    },
         |    {
         |      "name": "PAYMENT.CAPTURE.COMPLETED"
         |    },
         |    {
         |      "name": "PAYMENT.CAPTURE.DENIED"
         |    }
         |  ]
         |}
         |""".stripMargin
    writer.write(body)
    writer.flush
    writer.close
    httpConn.getOutputStream.close

    val responseStream = if (httpConn.getResponseCode / 100 eq 2) httpConn.getInputStream
    else httpConn.getErrorStream
    val s = new Scanner(responseStream).useDelimiter("\\A")
    val sb = new StringBuilder
    while (s.hasNext)
      val t = s.next
      sb.append(t)
    return sb.toString()
  }
}
