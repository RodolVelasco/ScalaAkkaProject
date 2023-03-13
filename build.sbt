name := "scala-akka-steam-project"

version := "0.1"

scalaVersion := "2.13.8"

/*lazy val akkaVersion = "2.6.20" // must be 2.5.13 so that it's compatible with the stores plugins (JDBC and Cassandra)
//lazy val akkaVersion = "2.7.0" // must be 2.5.13 so that it's compatible with the stores plugins (JDBC and Cassandra)
lazy val cassandraVersion = "1.0.6"
lazy val json4sVersion = "3.2.11"
lazy val protobufVersion = "3.6.1"
lazy val logBackVersion = "1.2.10"

// some libs are available in Bintray's JCenter
resolvers += Resolver.jcenterRepo

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
  // Cassandra
  "com.typesafe.akka" %% "akka-persistence-cassandra" % cassandraVersion,
  "com.typesafe.akka" %% "akka-persistence-cassandra-launcher" % cassandraVersion % Test,

  // Google Protocol Buffers
  "com.google.protobuf" % "protobuf-java"  % protobufVersion,

  //test
  "com.typesafe.akka" %% "akka-persistence-query" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-sharding-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-coordination" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
  "ch.qos.logback"    % "logback-classic" % logBackVersion,

)*/

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

  // alpakka
  "com.lightbend.akka" %% "akka-stream-alpakka-file" % "3.0.4",
  "com.lightbend.akka" %% "akka-stream-alpakka-json-streaming" % "4.0.0",
  "com.lightbend.akka" %% "akka-stream-alpakka-cassandra" % "4.0.0",

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
