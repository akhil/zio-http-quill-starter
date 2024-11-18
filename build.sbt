ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.3"

lazy val root = (project in file("."))
  .settings(
    name := "zio-http-quill-starter"
  )


// https://mvnrepository.com/artifact/com.konghq/unirest-modules-gson
libraryDependencies += "com.konghq" % "unirest-modules-gson" % "4.4.4"

//libraryDependencies ++= List("com.softwaremill.sttp.client3" %% "core" % "3.9.8")

libraryDependencies += "org.polyvariant" %% "sttp-oauth2" % "0.19.2"
libraryDependencies += "org.polyvariant" %% "sttp-oauth2-circe" % "0.19.2" // Or other, see JSON support
// libraryDependencies += "org.polyvariant" %% "sttp-oauth2-cache-zio" % "0.19.2" // Or other, see JSON support

libraryDependencies ++= Seq(
  "io.getquill" %% "quill-jdbc-zio" % "4.8.5",
  // https://mvnrepository.com/artifact/com.mysql/mysql-connector-j
  "com.mysql" % "mysql-connector-j" % "9.0.0",
  // https://mvnrepository.com/artifact/io.getquill/quill-codegen-jdbc
  //"io.getquill" %% "quill-codegen-jdbc" % "4.8.4"
)

libraryDependencies ++= Seq(
  "dev.zio" %% "zio-test"          % "2.1.9" % Test,
  "dev.zio" %% "zio-test-sbt"      % "2.1.9" % Test,
  "dev.zio" %% "zio-test-magnolia" % "2.1.9" % Test
)
// https://mvnrepository.com/artifact/com.zaxxer/HikariCP
libraryDependencies += "com.zaxxer" % "HikariCP" % "5.1.0"

// https://mvnrepository.com/artifact/dev.zio/zio-http
libraryDependencies += "dev.zio" %% "zio-http" % "3.0.0-RC10"
