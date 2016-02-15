name := "oneplusone"

version := "1.0"

scalaVersion := "2.11.7"

resolvers += Resolver.bintrayRepo("hmrc", "releases")

val spray = "1.3.3"

libraryDependencies ++= Seq(
  "uk.gov.hmrc"         %% "emailaddress"   % "1.1.0",
  "io.spray"            %% "spray-can"      % spray,
  "io.spray"            %% "spray-client"   % spray,
  "io.spray"            %% "spray-routing"  % spray,
  "org.json4s"          %% "json4s-jackson" % "3.3.0",
  "com.typesafe.akka"   %% "akka-actor"     % "2.3.9",
  "org.specs2"          %% "specs2-core"    % "2.3.11" % "test"
)

lazy val root =
  project.in(file("."))
    .enablePlugins(SbtTwirl)

unmanagedSourceDirectories in Compile += (sourceDirectory in Compile).value / "twirl"