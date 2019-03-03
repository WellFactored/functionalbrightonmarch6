package v1.connectors
import com.google.inject.name.Named
import javax.inject.Inject
import models.DrivingLicence
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

import scala.concurrent.{ExecutionContext, Future}

class DVLAConnector @Inject()(wsClient: WSClient, @Named("dvla.host") dvlaHost: String, @Named("dvla.port") dvlaPort:Int) {
  def getLicence(drivingLicenceNumber: String)(implicit ec: ExecutionContext): Future[Option[DrivingLicence]] =
    wsClient.url(s"http://$dvlaHost:$dvlaPort/licence/$drivingLicenceNumber").get.map { response =>
      response.status match {
        case 404 => None
        case 200 => Some(Json.parse(response.body).as[DrivingLicence])
        case _   => throw new Exception(s"unexpected response ${response.status} from call to dvla")
      }
    }
}
