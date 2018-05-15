name := "inventory"
version := "0.0.1"
scalaVersion := "2.12.6"

libraryDependencies ++= {
  val akkaHttpVersion = "10.1.1"
  val circeVersion    = "0.10.0-M1"
  val monixVersion    = "3.0.0-RC1"
  Seq(
    "com.typesafe.akka"          %% "akka-http"         % akkaHttpVersion,
    "com.typesafe.akka"          %% "akka-http-testkit" % akkaHttpVersion % Test,
    "com.typesafe.akka"          %% "akka-stream"       % "2.5.11",
    "org.scalatest"              %% "scalatest"         % "3.0.5" % "test",
    "org.typelevel"              %% "cats-core"         % "1.1.0",
    "io.circe"                   %% "circe-core"        % circeVersion,
    "io.circe"                   %% "circe-core"        % circeVersion,
    "io.circe"                   %% "circe-generic"     % circeVersion,
    "io.circe"                   %% "circe-parser"      % circeVersion,
    "io.circe"                   %% "circe-java8"       % circeVersion,
    "de.heikoseeberger"          %% "akka-http-circe"   % "1.20.1",
    "io.monix"                   %% "monix"             % monixVersion,
    "com.typesafe.scala-logging" %% "scala-logging"     % "3.9.0",
    "com.github.pureconfig"      %% "pureconfig"        % "0.9.1",
    "org.slf4j"                  % "slf4j-simple"       % "1.6.2",
    "ch.megard"                  %% "akka-http-cors"    % "0.3.0"
  )
}

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:experimental.macros",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ypartial-unification",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Ywarn-unused-import",
  "-Xfuture"
)

coverageExcludedPackages := "<empty>;com.jupco.hackdo.infrastructure.clients.impl.MockedUsersClient;com.jupco.hackdo.Main"
scalafmtOnCompile := true
