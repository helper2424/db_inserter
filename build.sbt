name := "db_inserter"

version := "1.0"

scalaVersion := "2.12.0"
allDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.12"
libraryDependencies += "postgresql" % "postgresql" % "9.1-901.jdbc4"
libraryDependencies += "com.github.scopt" %% "scopt" % "3.5.0"