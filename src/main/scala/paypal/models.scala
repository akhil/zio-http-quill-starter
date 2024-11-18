package paypal

package models

import io.getquill.*
import io.getquill.jdbczio.*
import zio.*

import java.sql.SQLException

case class PaypalWebhookData(id: String,
                             createdTime: String,
                             resourceType: String,
                             eventType: String,
                             summary: String,
                             eventVersion: Option[String],
                             resourceVersion: Option[String],
                             resource: String,
                             links: Option[String],
                             _rawdata: String)

class DataService(quill: Quill.Mysql[SnakeCase]) {
  import quill._
  private val tableName = "PAYPAL_WEBHOOK_DATA"
  def getAllPaypalWebhookData: ZIO[Any, SQLException, List[PaypalWebhookData]] =
    run(querySchema[PaypalWebhookData]("PAYPAL_WEBHOOK_DATA"))

  def savePaypalWebhookData(paypalWebhookData: PaypalWebhookData): ZIO[Any, SQLException, Long] =
    run(quote(querySchema[PaypalWebhookData]("PAYPAL_WEBHOOK_DATA").insertValue(lift(paypalWebhookData))))
}

object DataService {
  def getAllPaypalWebhookData: ZIO[DataService, SQLException, List[PaypalWebhookData]] =
    ZIO.serviceWithZIO[DataService](_.getAllPaypalWebhookData)


  def savePaypalWebhookData(paypalWebhookData: PaypalWebhookData): ZIO[DataService, SQLException, Long] =
    ZIO.serviceWithZIO[DataService](_.savePaypalWebhookData(paypalWebhookData))


  val live: ZLayer[Quill.Mysql[SnakeCase], Nothing, DataService] = ZLayer.fromFunction(new DataService(_))
}