package models
import play.api.libs.json.{Json, OFormat}

case class PersonWithLicence(person: Person, drivingLicence: Option[DrivingLicence])

object PersonWithLicence {
  implicit val formats: OFormat[PersonWithLicence] = Json.format
}
