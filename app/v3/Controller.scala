package v3

import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class Controller @Inject()(
  service:                           PersonService[Future],
  override val controllerComponents: ControllerComponents
)(
  implicit ec: ExecutionContext
) extends BaseController {

  def getPerson(name: String): Action[AnyContent] = Action.async { implicit request =>
    service.getPerson(name).map {
      case Some(person) => Ok(Json.toJson(person))
      case None         => NotFound
    }
  }
}
