package v1

import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext

class PersonController @Inject()(
  service: PersonService,
  cc:      MessagesControllerComponents
)(
  implicit ec: ExecutionContext
) extends MessagesAbstractController(cc) {

  def getPerson(name: String): Action[AnyContent] = Action.async { implicit request =>
    service.getPerson(name).map {
      case Some(person) => Ok(Json.toJson(person))
      case None         => NotFound
    }
  }
}
