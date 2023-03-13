name := "scala-akka-steam-project"

version := "0.1"

scalaVersion := "2.13.8"

val akkaVersion = "2.6.20"
val akkaPersistenceCassandraVersion = "1.0.5"
val akkaProjectionVersion = "1.2.4"
val akkaHttpVersion = "10.2.9"
val logBackVersion = "1.2.10"

libraryDependencies ++= Seq(
  // streams
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,

  // http
  "com.typesafe.akka" %% "akka-http"   % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion,

  // persistence
  "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence-query" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence-typed" % akkaVersion,

  // serialization
  "com.typesafe.akka" %% "akka-serialization-jackson" % akkaVersion,

  // cassandra
  "com.typesafe.akka" %% "akka-persistence-cassandra" % akkaPersistenceCassandraVersion,
  "com.typesafe.akka" %% "akka-persistence-cassandra-launcher" % akkaPersistenceCassandraVersion % Test,
  "com.lightbend.akka" %% "akka-projection-cassandra" % akkaProjectionVersion,

  // cluster sharding
  "com.typesafe.akka" %% "akka-cluster-sharding-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-coordination" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,

  // logback
  "ch.qos.logback"    % "logback-classic" % logBackVersion,



  // google protocol buffers
  "com.google.protobuf" % "protobuf-java" % "3.21.5"
)
