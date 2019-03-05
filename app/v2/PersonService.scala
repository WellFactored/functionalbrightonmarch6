package v2

import com.google.inject.ImplementedBy
import javax.inject.Inject
import models.{DrivingLicence, Person, PersonWithLicence}

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[ProdPersonService])
trait PersonService {
  def getPerson(name: String)(implicit ec: ExecutionContext): Future[Option[PersonWithLicence]]
}

class ProdPersonService @Inject()(personRepository: PersonRepository, dvlaConnector: DVLAConnector) extends PersonService {
  def getPerson(name: String)(implicit ec: ExecutionContext): Future[Option[PersonWithLicence]] =
    for {
      person         <- personRepository.get(name)
      drivingLicense <- person.map(getLicence).getOrElse(Future.successful(None))
    } yield person.map(PersonWithLicence(_, drivingLicense))

  private def getLicence(person: Person)(implicit ec: ExecutionContext): Future[Option[DrivingLicence]] =
    person.drivingLicenceNumber match {
      case None           => Future.successful(None)
      case Some(dlNumber) => dvlaConnector.getLicence(dlNumber)
    }
}
