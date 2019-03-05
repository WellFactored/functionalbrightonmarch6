package v3

import javax.inject.{Inject, Singleton}
import models.Person
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

trait PersonRepository[F[_]] {
  def get(name: String): F[Option[Person]]
}

@Singleton
class FuturePersonRepository @Inject()(
  dbConfigProvider: DatabaseConfigProvider
)(
  implicit ec: ExecutionContext
) extends PersonRepository[Future] {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  /**
    * Here we define the table. It will have a name of people
    */
  private class PeopleTable(tag: Tag) extends Table[Person](tag, "people") {

    /** The ID column, which is the primary key, and auto incremented */
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name: Rep[String] = column[String]("name", O.Unique)

    def age: Rep[Int] = column[Int]("age")

    def drivingLicenceNumber: Rep[Option[String]] =
      column[Option[String]]("driving_licence_number")

    def * =
      (id, name, age, drivingLicenceNumber) <> ((Person.apply _).tupled, Person.unapply)
  }

  /**
    * The starting point for all queries on the people table.
    */
  private val people = TableQuery[PeopleTable]

  def get(name: String): Future[Option[Person]] =
    db.run(people.filter(_.name === name).result.headOption)

  def create(name: String, age: Int, drivingLicenseNumber: Option[String]): Future[Person] =
    db.run {
      (people.map(p => (p.name, p.age, p.drivingLicenceNumber))
        returning people.map(_.id)
        into ((cols, id) => Person(id, cols._1, cols._2, cols._3))) += (name, age, drivingLicenseNumber)
    }

  def delete(id: Long): Future[Unit] =
    db.run(people.filter(_.id === id).delete).map(_ => ())

}
