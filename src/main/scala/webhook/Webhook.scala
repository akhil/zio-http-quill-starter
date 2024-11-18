package webhook

import paypal.models.PaypalWebhookData
import zio.http.*
import zio.*
import com.google.gson.*
import com.zaxxer.hikari.HikariDataSource
import io.getquill.*
import io.getquill.jdbczio.*
import io.getquill.util.LoadConfig

import java.sql.SQLException
import paypal.models.DataService

object Webhook extends ZIOAppDefault {

  def parsePaypalWebhook(body: String): PaypalWebhookData = {
    val gson = new Gson
    val json = gson.fromJson(body, classOf[JsonObject])
    try {
      PaypalWebhookData(
        id = json.get("id").getAsString,
        createdTime = json.get("create_time").getAsString,
        resourceType = json.get("resource_type").getAsString,
        eventType = json.get("event_type").getAsString,
        summary = json.get("summary").getAsString,
        eventVersion = Option(json.get("event_version")).map(_.getAsString),
        resourceVersion = Option(json.get("resource_version")).map(_.getAsString),
        resource = json.get("resource").getAsJsonObject.toString,
        links = Option(json.get("links")).map(_.getAsJsonArray.toString),
        _rawdata = body
      )
    } catch {
      case e: Exception =>
        println(body)
        println(e)
        e.printStackTrace()
        throw e
    }
  }
  def dataSource: HikariDataSource = JdbcContextConfig(LoadConfig("database")).dataSource

  private def save(paypalWebhookData: PaypalWebhookData): Task[Long] = {
    val data = DataService.savePaypalWebhookData(paypalWebhookData)
      .provide(DataService.live,
        Quill.Mysql.fromNamingStrategy(SnakeCase),
        Quill.DataSource.fromDataSource(dataSource))
    data
  }
  private val routes: Routes[Any, Response] =
    Routes(
      Method.GET / "hello" ->
        handler(Response.text("Hello, World!")),

      Method.POST / "webhook" -> handler { (request: Request) =>
        ZIO.attempt {
          val headers = request.headers
          for {
            body <- request.body.asString
            payload = parsePaypalWebhook(body)
            result <- save(payload)
            response = s"Webhook processed, ID: $result"
          } yield {
            Response.text(response)
          }
        }.flatten.catchAll { error =>
          println(error)
          ZIO.succeed(Response.text(s"Error: ${error.getMessage}").status(Status.InternalServerError))
        }
      }
    )

  override def run: ZIO[ZIOAppArgs & Scope, Throwable, Any] =
    Server.serve(routes).provide(Server.default)
}
