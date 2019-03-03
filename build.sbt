name := """functionalbrightonmarch26"""

version := "2.7.x"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  guice,
  ws,
  "com.typesafe.play"      %% "play-slick"            % "4.0.0",
  "com.typesafe.play"      %% "play-slick-evolutions" % "4.0.0",
  "org.typelevel"          %% "cats-core"             % "1.6.0",
  "org.scalatest"          %% "scalatest"             % "3.0.5",
  "com.h2database"         % "h2"                     % "1.4.198",
  "org.scalatestplus.play" %% "scalatestplus-play"    % "3.1.2" % "test",
  "com.github.tomakehurst" % "wiremock-jre8"          % "2.21.0" % "test",
)

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-language:higherKinds",
  "-Xfatal-warnings"
)
