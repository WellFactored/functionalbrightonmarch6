package v2.services
import models.{DrivingLicence, Person}
import org.scalatest.{Matchers, OptionValues, WordSpecLike}
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import v2.connectors.DVLAConnector
import v2.repositories.PersonRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class PersonServiceTestV2 extends WordSpecLike with Matchers with OptionValues with FutureAwaits with DefaultAwaitTimeout {
  val name     = "test-name"
  val dlNumber = "test-dl-number"

  def repo(result: Person): PersonRepository = new PersonRepository {
    override def get(name: String)(implicit ec: ExecutionContext): Future[Option[Person]] = Future.successful(Some(result))
  }

  def connector(name: String, result: Option[DrivingLicence]): DVLAConnector = new DVLAConnector {
    override def getLicence(drivingLicenceNumber: String)(implicit ec: ExecutionContext): Future[Option[DrivingLicence]] =
      if (drivingLicenceNumber == dlNumber) Future.successful(result) else Future.successful(None)
  }

  "getPerson" should {
    "return a person with no driving licence details when the database entry has no driving licence number" in {
      val service = new ProdPersonService(repo(Person(0, name, 42, None)), connector(name, None))
      await(service.getPerson(name)).value.drivingLicence shouldBe None
    }

    "return a person with no driving licence details when the database entry has a driving licence number but the dvla doesn't know about it" in {
      val service = new ProdPersonService(repo(Person(0, name, 42, Some(dlNumber))), connector(name, None))
      await(service.getPerson(name)).value.drivingLicence shouldBe None
    }

    "return a person with driving license details when the database entry has a driving license number and the dvla has details" in {
      val expectedDl = DrivingLicence(name, dlNumber, "", provisional = false, hgv = false)

      val service = new ProdPersonService(repo(Person(0, name, 42, Some(dlNumber))), connector(name, Some(expectedDl)))
      await(service.getPerson(name)).value.drivingLicence.value shouldBe expectedDl
    }
  }
}
