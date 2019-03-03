package v3.services

import cats.Id
import models.{DrivingLicence, Person}
import org.scalatest.{Matchers, OptionValues, WordSpecLike}
import v3.connectors.DVLAConnector
import v3.repositories.PersonRepository

class PersonServiceTestV3 extends WordSpecLike with Matchers with OptionValues {
  val name     = "test-name"
  val dlNumber = "test-dl-number"

  def repo(result: Person): PersonRepository[Id] =
    (name: String) => Some(result)

  def connector(name: String, result: Option[DrivingLicence]): DVLAConnector[Id] =
    (drivingLicenceNumber: String) => if (drivingLicenceNumber == dlNumber) result else None

  "getPerson" should {
    "return a person with no driving licence details when the database entry has no driving licence number" in {
      val service = new PersonServiceImpl[Id](repo(Person(0, name, 42, None)), connector(name, None))
      service.getPerson(name).value.drivingLicence shouldBe None
    }

    "return a person with no driving licence details when the database entry has a driving licence number but the dvla doesn't know about it" in {
      val service = new PersonServiceImpl(repo(Person(0, name, 42, Some(dlNumber))), connector(name, None))
      service.getPerson(name).value.drivingLicence shouldBe None
    }

    "return a person with driving license details when the database entry has a driving license number and the dvla has details" in {
      val expectedDl = DrivingLicence(name, dlNumber, "", provisional = false, hgv = false)

      val service = new PersonServiceImpl(repo(Person(0, name, 42, Some(dlNumber))), connector(name, Some(expectedDl)))
      service.getPerson(name).value.drivingLicence.value shouldBe expectedDl
    }
  }
}
