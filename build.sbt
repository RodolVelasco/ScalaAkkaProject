name := "scala-akka-steam-project"

version := "0.1"

scalaVersion := "2.13.8"

lazy val akkaVersion = "2.6.20"
lazy val akkaPersistenceCassandraVersion = "1.0.5"
lazy val akkaProjectionVersion = "1.2.4"
lazy val akkaHttpVersion = "10.2.9"
lazy val protobufVersion = "3.21.5"
lazy val logBackVersion = "1.2.10"
lazy val akkaStreamAlpakkaFile = "3.0.4"
lazy val akkaStreamAlpakka = "4.0.0"
lazy val sprayJsonVersion = "1.3.6"
lazy val esriGeometryApiVersion = "2.2.4"
lazy val datastaxOssVersion = "4.15.0"
lazy val lihaoyiVersion = "0.9.1"

libraryDependencies ++= Seq(
  // akka core
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,

  // streams
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,

  // http
  "com.typesafe.akka" %% "akka-http"   % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion,

  // persistence
  "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence-query" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence-typed" % akkaVersion,

  // cassandra
  "com.typesafe.akka" %% "akka-persistence-cassandra" % akkaPersistenceCassandraVersion,
  "com.typesafe.akka" %% "akka-persistence-cassandra-launcher" % akkaPersistenceCassandraVersion % Test,
  "com.lightbend.akka" %% "akka-projection-cassandra" % akkaProjectionVersion,

  // remote cluster sharding
  "com.typesafe.akka" %% "akka-remote" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-sharding-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-coordination" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-typed" % akkaVersion,

  // serialization frameworks
  //"com.github.romix.akka" %% "akka-kryo-serialization" % "0.5.1",
  //"com.sksamuel.avro4s" %% "avro4s-core" % "2.0.4",
  //"com.google.protobuf" % "protobuf-java" % protobufVersion,
  //"com.typesafe.akka" %% "akka-serialization-jackson" % akkaVersion,

  "io.spray" %%  "spray-json" % sprayJsonVersion,

  "com.esri.geometry" % "esri-geometry-api" % esriGeometryApiVersion,

  "com.datastax.oss"  %  "java-driver-core"  % datastaxOssVersion,

  "com.lihaoyi" %% "os-lib" % lihaoyiVersion,

  // alpakka
  "com.lightbend.akka" %% "akka-stream-alpakka-file" % akkaStreamAlpakkaFile,
  "com.lightbend.akka" %% "akka-stream-alpakka-json-streaming" % akkaStreamAlpakka,
  "com.lightbend.akka" %% "akka-stream-alpakka-cassandra" % akkaStreamAlpakka,

  //logback
  "ch.qos.logback"    % "logback-classic" % logBackVersion,

)