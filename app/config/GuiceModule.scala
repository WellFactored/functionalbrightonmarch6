package config
import com.google.inject.name.Names.named
import com.google.inject.{AbstractModule, TypeLiteral}
import play.api.{Configuration, Environment}
import v3.connectors.{DVLAConnector, FutureDVLAConnector}
import v3.repositories.{FuturePersonRepository, PersonRepository}
import v3.services.{FuturePersonService, PersonService}

import scala.concurrent.Future

class GuiceModule(environment: Environment, configuration: Configuration) extends AbstractModule {

  override def configure(): Unit = {
    bindConfigString("dvla.host")
    bindConfigInt("dvla.port")

    bind(new TypeLiteral[PersonService[Future]] {}).to(classOf[FuturePersonService])
    bind(new TypeLiteral[DVLAConnector[Future]] {}).to(classOf[FutureDVLAConnector])
    bind(new TypeLiteral[PersonRepository[Future]] {}).to(classOf[FuturePersonRepository])
  }

  private def bindConfigString(path: String): Unit =
    bindConstant()
      .annotatedWith(named(path))
      .to(configuration.underlying.getString(path))

  private def bindConfigInt(path: String): Unit =
    bindConstant()
      .annotatedWith(named(path))
      .to(configuration.underlying.getInt(path))
}
