package v1.repositories

import javax.inject.{Inject, Singleton}
import models.Person
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

/**
  * A repository for people.
  *
  * @param dbConfigProvider The Play db config provider. Play will inject this for you.
  */
@Singleton
class PersonRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(
  implicit ec:                                     ExecutionContext
) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  /**
    * Here we define the table. It will have a name of people
    */
  private class PeopleTable(tag: Tag) extends Table[Person](tag, "people") {

    /** The ID column, which is the primary key, and auto incremented */
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name: Rep[String] = column[String]("name")

    def age:Rep[Int] = column[Int]("age")

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

  /**
    * List all the people in the database.
    */
  def list(): Future[Seq[Person]] = db.run {
    people.result
  }
}