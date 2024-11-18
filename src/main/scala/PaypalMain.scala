import com.google.gson._
object PaypalMain extends App {
  val tokenString = PaypalCharge.getAuthToken()
  //println(tokenString)
  
  val accessToken = PaypalCharge.extractAccessToken(tokenString)
  println(accessToken)
  
  val charges = PaypalCharge.getCharges(accessToken)
  println(charges)

  val webhook = PaypalCharge.createWebhook(accessToken)
  println(webhook)
}