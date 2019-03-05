package v3

import com.google.inject.name.Named
import javax.inject.Inject
import models.DrivingLicence
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

import scala.concurrent.{ExecutionContext, Future}

trait DVLAConnector[F[_]] {
  def getLicence(drivingLicenceNumber: String): F[Option[DrivingLicence]]
}

class FutureDVLAConnector @Inject()(
  wsClient:                     WSClient,
  @Named("dvla.host") dvlaHost: String,
  @Named("dvla.port") dvlaPort: Int
)(
  implicit ec: ExecutionContext
) extends DVLAConnector[Future] {
  def getLicence(drivingLicenceNumber: String): Future[Option[DrivingLicence]] =
    wsClient.url(s"http://$dvlaHost:$dvlaPort/licence/$drivingLicenceNumber").get.map { response =>
      response.status match {
        case 404 => None
        case 200 => Some(Json.parse(response.body).as[DrivingLicence])
        case _   => throw new Exception(s"unexpected response ${response.status} from call to dvla")
      }
    }
}
