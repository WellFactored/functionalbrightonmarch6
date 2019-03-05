package config
import com.google.inject.name.Names.named
import com.google.inject.{AbstractModule, TypeLiteral}
import play.api.{Configuration, Environment}

import scala.concurrent.Future

class GuiceModule(environment: Environment, configuration: Configuration) extends AbstractModule {

  override def configure(): Unit = {
    bindConfigString("dvla.host")
    bindConfigInt("dvla.port")

    bind(new TypeLiteral[v3.PersonService[Future]] {}).to(classOf[v3.FuturePersonService])
    bind(new TypeLiteral[v3.DVLAConnector[Future]] {}).to(classOf[v3.FutureDVLAConnector])
    bind(new TypeLiteral[v3.PersonRepository[Future]] {}).to(classOf[v3.FuturePersonRepository])
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
