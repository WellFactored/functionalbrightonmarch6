package models
import play.api.libs.json.{Json, OFormat}

case class DrivingLicence(name: String, address: String, provisional: Boolean, hgv: Boolean)

object DrivingLicence {
  implicit val formats: OFormat[DrivingLicence] = Json.format
}

