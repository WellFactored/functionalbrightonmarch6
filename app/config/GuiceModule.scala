package config
import com.google.inject.AbstractModule
import com.google.inject.name.Names.named
import play.api.{Configuration, Environment}

class GuiceModule(environment: Environment, configuration: Configuration) extends AbstractModule {

  override def configure(): Unit =
    bindConfigString("dvlaHost")


  private def bindConfigString(path: String): Unit =
    bindConstant()
      .annotatedWith(named(path))
      .to(configuration.underlying.getString(path))
}
