import zio.*
import zio.test.*
import zio.test.Assertion.*

import scala.io.Source
import webhook.Webhook

object ParsePaypalWebhookDataSpec extends ZIOSpecDefault {
  def spec = suite("ParsePaypalWebhookData")(
      test("Happy Path") {
        val body = Source.fromResource("payment_capture_completed.json").mkString
        val parsed = Webhook.parsePaypalWebhook(body)
        //println(parsed.summary)
        assert(parsed.summary)(equalTo("Payment completed for $ 30.0 USD"))
      }
    )
}