ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

val akkaVersion = "2.6.19"
val akkaHttpVersion = "10.2.9"
val logbackVersion = "1.2.9"

lazy val root = (project in file("."))
  .settings(
    name := "akka_video_streaming",
    libraryDependencies ++= Seq(
      // actor
      "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
      // http
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      // streams
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream-typed" % akkaVersion,
      // serialization
      "com.typesafe.akka" %% "akka-serialization-jackson" % akkaVersion,
      // logging
      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
      "ch.qos.logback" % "logback-classic" % logbackVersion,
      "org.scalatra.scalate" %% "scalate-core" % "1.9.6"
    )
  )