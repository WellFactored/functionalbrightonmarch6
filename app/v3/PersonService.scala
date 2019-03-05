package v3

import cats.Monad
import cats.implicits._
import javax.inject.Inject
import models.{DrivingLicence, Person, PersonWithLicence}

import scala.concurrent.{ExecutionContext, Future}

trait PersonService[F[_]] {
  def getPerson(name: String): F[Option[PersonWithLicence]]
}

class PersonServiceImpl[F[_]: Monad] @Inject()(
  personRepository: PersonRepository[F],
  dvlaConnector:    DVLAConnector[F]
) extends PersonService[F] {
  def getPerson(name: String): F[Option[PersonWithLicence]] =
    for {
      person         <- personRepository.get(name)
      drivingLicense <- person.map(getLicence).getOrElse(Monad[F].pure(None))
    } yield person.map(PersonWithLicence(_, drivingLicense))

  private def getLicence(person: Person): F[Option[DrivingLicence]] =
    person.drivingLicenceNumber match {
      case None           => Monad[F].pure(None)
      case Some(dlNumber) => dvlaConnector.getLicence(dlNumber)
    }
}

class FuturePersonService @Inject()(
  personRepository: PersonRepository[Future],
  dvlaConnector:    DVLAConnector[Future]
)(
  implicit ec: ExecutionContext
) extends PersonServiceImpl[Future](personRepository, dvlaConnector)
