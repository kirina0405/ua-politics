name := """hello-akka"""

version := "1.0"

scalaVersion := "2.11.6"

lazy val akkaVersion = "2.4.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "junit" % "junit" % "4.12" % "test",
  "com.novocode" % "junit-interface" % "0.11" % "test",
  "net.sourceforge.htmlcleaner" % "htmlcleaner" % "2.6.1",
  "org.reactivemongo" % "reactivemongo_2.11" % "0.11.12",
  "org.scalaj" % "scalaj-http_2.11" % "2.3.0",
  "org.slf4j" % "slf4j-api" % "1.7.21"
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")


fork in run := true