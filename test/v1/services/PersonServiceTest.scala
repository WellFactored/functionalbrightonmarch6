package v1.services
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, urlPathEqualTo}
import models.{DrivingLicence, PersonWithLicence}
import org.scalatest.OptionValues
import play.api.http.Status
import play.api.libs.json.Json
import v1.repositories.PersonRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PersonServiceTest extends BaseSpec with OptionValues {

  lazy val repo:    PersonRepository = app.injector.instanceOf[PersonRepository]
  lazy val service: PersonService    = app.injector.instanceOf[PersonService]

  private val name     = "test-name"
  private val dlNumber = "test-dl-number"

  "getPerson" should {
    "return a person with no driving licence details when the database entry has no driving licence number" in {
      val result: Future[Option[PersonWithLicence]] = for {
        _ <- repo.create(name, 42, None)

        // The actual line of test code
        maybePerson <- service.getPerson(name)

        // Be sure to tear down the inserted data so it doesn't interfere with subsequent tests
        _ <- maybePerson.map(p => repo.delete(p.person.id)).getOrElse(Future.successful(()))
      } yield maybePerson

      await(result).value.drivingLicence shouldBe None
    }

    "return a person with no driving licence details when the database entry has a driving licence number but the dvla doesn't know about it" in {
      wireMockServer.stubFor(
        get(urlPathEqualTo(s"/licence/$dlNumber"))
          .willReturn(aResponse().withStatus(Status.NOT_FOUND)))

      val result: Future[Option[PersonWithLicence]] = for {
        _ <- repo.create(name, 42, Some(dlNumber))

        // The actual line of test code
        maybePerson <- service.getPerson(name)

        // Be sure to tear down the inserted data so it doesn't interfere with subsequent tests
        _ <- maybePerson.map(p => repo.delete(p.person.id)).getOrElse(Future.successful(()))
      } yield maybePerson

      await(result).value.drivingLicence shouldBe None
    }

    "return a person with driving license details when the database entry has a driving license number and the dvla has details" in {
      val repo                   = app.injector.instanceOf[PersonRepository]
      val expectedDrivingLicense = DrivingLicence(name, dlNumber, "", provisional = false, hgv = false)

      wireMockServer.stubFor(
        get(urlPathEqualTo(s"/licence/$dlNumber"))
          .willReturn(
            aResponse()
              .withStatus(Status.OK)
              .withBody(
                Json.prettyPrint(Json.toJson(expectedDrivingLicense))
              )))

      val result: Future[Option[PersonWithLicence]] = for {
        _ <- repo.create(name, 42, Some(dlNumber))

        // The actual line of test code
        maybePerson <- service.getPerson(name)

        // Be sure to tear down the inserted data so it doesn't interfere with subsequent tests
        _ <- maybePerson.map(p => repo.delete(p.person.id)).getOrElse(Future.successful(()))
      } yield maybePerson

      await(result).value.drivingLicence.value shouldBe expectedDrivingLicense
    }
  }
}
