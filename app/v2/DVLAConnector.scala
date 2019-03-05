package v2

import com.google.inject.ImplementedBy
import com.google.inject.name.Named
import javax.inject.Inject
import models.DrivingLicence
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[ProdDVLAConnector])
trait DVLAConnector {
  def getLicence(drivingLicenceNumber: String)(implicit ec: ExecutionContext): Future[Option[DrivingLicence]]
}

class ProdDVLAConnector @Inject()(
  wsClient:                     WSClient,
  @Named("dvla.host") dvlaHost: String,
  @Named("dvla.port") dvlaPort: Int
) extends DVLAConnector {
  def getLicence(drivingLicenceNumber: String)(implicit ec: ExecutionContext): Future[Option[DrivingLicence]] =
    wsClient.url(s"http://$dvlaHost:$dvlaPort/licence/$drivingLicenceNumber").get.map { response =>
      response.status match {
        case 404 => None
        case 200 => Some(Json.parse(response.body).as[DrivingLicence])
        case _   => throw new Exception(s"unexpected response ${response.status} from call to dvla")
      }
    }
}
