name := "inventory"
version := "0.0.1"
scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http"         % "10.1.1",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.1.1" % Test,
  "com.typesafe.akka" %% "akka-stream"       % "2.5.11",
  "org.scalatest"     %% "scalatest"         % "3.0.5" % "test"
)

scalafmtOnCompile := true
