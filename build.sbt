name := "it-wknd-streams"

version := "1.0"

scalaVersion := "2.12.2"

libraryDependencies ++= {
  val akkaVersion = "2.4.17"
  val httpVersion = "10.0.6"
  Seq(
    "com.typesafe.akka" %% "akka-actor"      % akkaVersion,
    "com.typesafe.akka" %% "akka-stream"     % akkaVersion,
    "com.typesafe.akka" %% "akka-http"       % httpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json"  % httpVersion,
    "com.typesafe.akka" %% "akka-slf4j"      % akkaVersion,
    "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided",
    "com.softwaremill.macwire" %% "macrosakka" % "2.3.0" % "provided",
    "ch.qos.logback"    %  "logback-classic" % "1.2.3"
  )
}
